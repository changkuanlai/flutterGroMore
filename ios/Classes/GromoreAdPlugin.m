#import "GromoreAdPlugin.h"

#if __has_include(<gromore_ad/gromore_ad-Swift.h>)
#import <gromore_ad/gromore_ad-Swift.h>
#else
#import "gromore_ad-Swift.h"
#endif

 @implementation GromoreAdPlugin
+(void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
//    FlutterMethodChannel* channel = [FlutterMethodChannel
//        methodChannelWithName:@"com.disk.native.receive/gromore_ad"
//              binaryMessenger:[registrar messenger]];
//    GromoreAdPlugin* instance = [[GromoreAdPlugin alloc] init];
//
//    [registrar addMethodCallDelegate:instance channel:channel];
    [GromoreAdPluginSwift registerWithRegistrar:registrar];
}

//- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
//  if ([@"getPlatformVersion" isEqualToString:call.method]) {
//    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
//  } else if ([@"getSDKVersion" isEqualToString:call.method]){
//     result([ABUAdSDKManager SDKVersion]);
//  }else if ([@"register" isEqualToString:call.method]){
//
//      [ABUPersonaliseConfigAdapter configAdapterWithKey:@"pangle" andBlock:^{
//
//       }];
//
//      NSDictionary * dict = call.arguments;
//      NSString * appId = dict[@"appId"];
//
//      [ABUPrivacyConfig setPrivacyWithKey:kABUPrivacyForbiddenCAID andValue:@(0)];
//      [ABUPrivacyConfig setPrivacyWithKey:kABUPrivacyLimitPersonalAds andValue:@(0)];
//
//      [ABUPrivacyConfig setPrivacyWithKey:kABUPrivacyLongitude andValue:@(0.5)];
//      [ABUPrivacyConfig setPrivacyWithKey:kABUPrivacyLatitude andValue:@(0.5)];
//
//      [ABUPrivacyConfig setPrivacyWithKey:kABUPrivacyNotAdult andValue:0];
//
//
//      [ABUAdSDKManager setupSDKWithAppId:appId config:^ABUUserConfig *(ABUUserConfig *c) {
//          c.logEnable = YES;
//           return c;
//      }];
//
//  }else if([@"showRewardVideoAd" isEqualToString:call.method]){
//      ABUDRewardVideoAdViewController *vc = [[ABUDRewardVideoAdViewController alloc] init];
//      vc.view.backgroundColor = [UIColor whiteColor];
//      [[UIApplication sharedApplication].keyWindow.rootViewController presentViewController:vc animated:YES completion:nil];
//   } else{
//    result(FlutterMethodNotImplemented);
//  }
//}
//- (void)testIDFA {
//    if (@available(iOS 14, *)) { // iOS14及以上版本需要先请求权限
//        [ATTrackingManager requestTrackingAuthorizationWithCompletionHandler:^(ATTrackingManagerAuthorizationStatus status) { // 获取到权限后，依然使用老方法获取idfa
//            if (status == ATTrackingManagerAuthorizationStatusAuthorized) {
//                NSString *idfa = [[ASIdentifierManager sharedManager].advertisingIdentifier UUIDString];
//                NSLog(@"%@chaors",idfa);
//            } else {
//                NSLog(@"请在设置-隐私-跟踪中允许App请求跟踪");
//            }}];
//        }
//    else { // iOS14以下版本依然使用老方法 // 判断在设置-隐私里用户是否打开了广告跟踪
//        if ([[ASIdentifierManager sharedManager] isAdvertisingTrackingEnabled]) {
//            NSString *idfa = [[ASIdentifierManager sharedManager].advertisingIdentifier UUIDString]; NSLog(@"%@",idfa);
//
//        } else {
//            NSLog(@"请在设置-隐私-广告中打开广告跟踪功能");
//        }
//
//    }
//}
@end
