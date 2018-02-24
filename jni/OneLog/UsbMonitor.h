#pragma once
#include <Windows.h>
#include <vector>

// This GUID is for all USB serial host PnP drivers, but you can replace it
// with any valid device class guid.
static const GUID WceusbshGUID = { 0x25dbce51, 0x6c8f, 0x4a72, 0x8a,0x6d,0xb5,0x4c,0x2b,0x4f,0xc8,0x35 };

/**
 * Call the java callback for given volume parameters
 */
void passVolumeToJava(std::wstring path, std::wstring volumeName, DWORD serialNumber,
                      std::wstring fileSystemName, DWORD maxComponentLen);

/**
 * Return a list of drive pathnames from a given mask
 */
std::vector<std::wstring> DrivesFromMask(DWORD unitmask);

/**
 * Pass a usb device at the given path to Java, along with information about it
 */
void handleVolume(std::wstring name);

/**
 * Handle Windows messages
 */
LRESULT CALLBACK WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam);

/**
 * Create a hidden window to receive usb device notifications, and listen for them.
 */
int createWindow();
