//
//  RCTSocialLogin.m
//  RNSocialLogin
//
//  Created by Shashank Singh on 27/04/22.
//

// RCTCalendarModule.m
#import "RCTSocialLogin.h"
#import <React/RCTLog.h>
@import GoogleSignIn;

@implementation RCTSocialLogin

// To export a module named RCTSocialLogin
RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(performGoogleLogin: (NSString *)name
                  location:(NSString *)location
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
  RCTLogInfo(@"Pretending to create an event");
  
  GIDConfiguration* signInConfig = [[GIDConfiguration alloc] initWithClientID:@"924524516869-8tcp8bk4olstc43pqnsf363nbe0kijjo.apps.googleusercontent.com"];
  
  [GIDSignIn.sharedInstance signInWithConfiguration:signInConfig
                           presentingViewController:RCTPresentedViewController()
                                             callback:^(GIDGoogleUser * _Nullable user,
                                                        NSError * _Nullable error) {
    
      if (error) {
        RCTLogInfo(@"error");
        reject(@"event_failed", @"No event id", error);
        return;
      }
    
    RCTLogInfo(@"Success");
    resolve(user);
      // If sign in succeeded, display the app's main content View.
    }];
  
}

@end
