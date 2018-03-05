// USB2.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "UsbMonitor.h"
#include <dbt.h>
#include <stdio.h>
#include <iostream>
#include <vector>
#include <string>

std::vector<std::wstring> DrivesFromMask(DWORD unitmask) {
	std::vector<std::wstring> result;
	std::wstring path(L"?:\\");
	DWORD localUnitmask(unitmask);
	for (wchar_t i = 0; i < 26; ++i) {
		if (0x01 == (localUnitmask & 0x1)) {
			path[0] = i + L'A';
			result.push_back(path);
		}
		localUnitmask >>= 1;
	}
	return result;
}

void handleVolume(std::wstring path) {
    TCHAR volumeName[MAX_PATH + 1] = { 0 };
    TCHAR fileSystemName[MAX_PATH + 1] = { 0 };
    DWORD serialNumber = 0;
    DWORD maxComponentLen = 0;
    DWORD fileSystemFlags = 0;
    if (GetVolumeInformation(
        path.c_str(),
        volumeName,
        ARRAYSIZE(volumeName),
        &serialNumber,
        &maxComponentLen,
        &fileSystemFlags,
        fileSystemName,
        ARRAYSIZE(fileSystemName)))
    {
        passVolumeToJava(path, volumeName, serialNumber, fileSystemName, maxComponentLen);
        // printf("Volume Name: %ws\n", volumeName);
        // printf("Serial Number: %lu\n", serialNumber);
        // printf("File System Name: %ws\n", fileSystemName);
        // printf("Max Component Length: %lu\n", maxComponentLen);
    }
	fflush(stdout);
}

LRESULT CALLBACK WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam) {
	// std::cout << "Callback" << std::endl;
	// std::cout << message << std::endl;
	switch (message) {
        case WM_CREATE: {
            // Set up notifications
            DEV_BROADCAST_DEVICEINTERFACE notificationFilter;
            ZeroMemory(&notificationFilter, sizeof(notificationFilter));
            notificationFilter.dbcc_size = sizeof(DEV_BROADCAST_DEVICEINTERFACE);
            notificationFilter.dbcc_devicetype = DBT_DEVTYP_DEVICEINTERFACE;
            notificationFilter.dbcc_classguid = WceusbshGUID;

            HDEVNOTIFY hDeviceNotify = RegisterDeviceNotification(
                hWnd,                       // events recipient
                &notificationFilter,        // type of device
                DEVICE_NOTIFY_WINDOW_HANDLE // type of recipient handle
            );

            if (hDeviceNotify == NULL) {
                std::cerr << "RegisterDeviceNotification failed" << std::endl;
                return 1;
            }
        }
        case WM_DEVICECHANGE: {
            if ((wParam == DBT_DEVICEARRIVAL) || (wParam == DBT_DEVICEREMOVECOMPLETE)) {
                DEV_BROADCAST_HDR* header = reinterpret_cast<DEV_BROADCAST_HDR*>(lParam);

                if (header->dbch_devicetype == DBT_DEVTYP_VOLUME && wParam == DBT_DEVICEARRIVAL)
                {
                    DEV_BROADCAST_VOLUME* devNot = reinterpret_cast<DEV_BROADCAST_VOLUME*>(lParam);
                    std::vector<std::wstring> drives = DrivesFromMask(devNot->dbcv_unitmask);
                    for (std::vector<std::wstring>::const_iterator it = drives.begin(); it != drives.end(); ++it)
                    {
                        handleVolume(*it);
                    }
                }
            }
        }
        default: {
            // Send all other messages on to the default windows handler.
            return DefWindowProc(hWnd, message, wParam, lParam);
	    }
	}
	return 0;
}

int createWindow() {
	HINSTANCE hInstance = ::GetModuleHandle(NULL);
	const wchar_t winClass[] = L"MyNotifyWindow";

	// Register WndProc
	WNDCLASSEX wcex;

	ZeroMemory(&wcex, sizeof(wcex));

	wcex.cbSize = sizeof(WNDCLASSEX);

	wcex.style = CS_HREDRAW | CS_VREDRAW;
	wcex.lpfnWndProc = WndProc;
	wcex.cbClsExtra = 0;
	wcex.cbWndExtra = 0;
	wcex.hInstance = hInstance;
	wcex.hIcon = NULL;
	wcex.hCursor = NULL;
	wcex.hbrBackground = (HBRUSH)(COLOR_WINDOW);
	wcex.lpszMenuName = NULL;
	wcex.lpszClassName = winClass;
	wcex.hIconSm = NULL;

	if (!RegisterClassEx(&wcex)) {
		std::cerr << "Registering window class failed" << std::endl;
		return 1;
	}


	// First create fake window
	const wchar_t winTitle[] = L"WindowTitle";
	HWND hWnd = ::CreateWindow(winClass, winTitle, WS_ICONIC, 0, 0,
		CW_USEDEFAULT, 0, NULL, NULL, hInstance, NULL);

	ShowWindow(hWnd, SW_HIDE);
	UpdateWindow(hWnd);

	MSG msg;
	int retVal;

	// Get all messages for any window that belongs to this thread,
	// without any filtering. Potential optimization could be
	// obtained via use of filter values if desired.

	while ((retVal = GetMessage(&msg, NULL, 0, 0)) != 0) {
		if (retVal == -1) {
			std::cerr << "Bad retval" << std::endl;
			break;
		} else {
			TranslateMessage(&msg);
			DispatchMessage(&msg);
		}
	}
}
