import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_gamepad/flutter_gamepad_method_channel.dart';

void main() {
  MethodChannelFlutterGamepad platform = MethodChannelFlutterGamepad();
  const MethodChannel channel = MethodChannel('flutter_gamepad');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
