import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_gamepad_method_channel.dart';

abstract class FlutterGamepadPlatform extends PlatformInterface {
  /// Constructs a FlutterGamepadPlatform.
  FlutterGamepadPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterGamepadPlatform _instance = MethodChannelFlutterGamepad();

  /// The default instance of [FlutterGamepadPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterGamepad].
  static FlutterGamepadPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterGamepadPlatform] when
  /// they register themselves.
  static set instance(FlutterGamepadPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
