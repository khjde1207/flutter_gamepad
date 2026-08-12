package com.example.flutter_gamepad

import android.content.Context
import android.hardware.input.InputManager
import android.view.InputDevice
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class FlutterGamepadPlugin :
  FlutterPlugin,
  MethodCallHandler,
  EventChannel.StreamHandler,
  InputManager.InputDeviceListener {
  private lateinit var channel: MethodChannel
  private lateinit var eventChannel: EventChannel
  private lateinit var inputManager: InputManager
  private var events: EventChannel.EventSink? = null

  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(binding.binaryMessenger, "com.example.flutter_gamepad/methods")
    channel.setMethodCallHandler(this)
    eventChannel = EventChannel(binding.binaryMessenger, "com.example.flutter_gamepad/events")
    eventChannel.setStreamHandler(this)
    inputManager = binding.applicationContext.getSystemService(Context.INPUT_SERVICE) as InputManager
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
      "gamepads" -> result.success(gamepads())
      "enableDebugMode", "disableDebugMode" -> result.success(null)
      else -> result.notImplemented()
    }
  }

  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    this.events = events
    inputManager.registerInputDeviceListener(this, null)
    gamepads().forEach { gamepad ->
      events?.success(mapOf("event" to "gamepadConnected", "gamepadId" to gamepad["id"], "gamepadInfo" to gamepad))
    }
  }

  override fun onCancel(arguments: Any?) {
    inputManager.unregisterInputDeviceListener(this)
    events = null
  }

  override fun onInputDeviceAdded(deviceId: Int) = emitConnection(deviceId, "gamepadConnected")
  override fun onInputDeviceRemoved(deviceId: Int) = emitConnection(deviceId, "gamepadDisconnected")
  override fun onInputDeviceChanged(deviceId: Int) = Unit

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    onCancel(null)
    channel.setMethodCallHandler(null)
    eventChannel.setStreamHandler(null)
  }

  private fun emitConnection(deviceId: Int, event: String) {
    val device = InputDevice.getDevice(deviceId) ?: return
    if (!device.isGamepad) return
    events?.success(mapOf("event" to event, "gamepadId" to deviceId, "gamepadInfo" to gamepadInfo(device)))
  }

  private fun gamepads(): List<Map<String, Any>> = InputDevice.getDeviceIds()
    .mapNotNull(InputDevice::getDevice)
    .filter(InputDevice::isGamepad)
    .map(::gamepadInfo)

  private fun gamepadInfo(device: InputDevice): Map<String, Any> = mapOf(
    "vendorName" to device.name,
    "productCategory" to "android",
    "isAttachedToDevice" to false,
    "id" to device.id,
  )

  private val InputDevice.isGamepad: Boolean
    get() = sources and (InputDevice.SOURCE_GAMEPAD or InputDevice.SOURCE_JOYSTICK) != 0
}
