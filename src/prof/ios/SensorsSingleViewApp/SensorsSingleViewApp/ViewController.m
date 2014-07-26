//
//  ViewController.m
//  SensorsSingleViewApp
//
//  Created by Alexey Pershin on 23/07/14.
//  Copyright (c) 2014 com.tcontrol. All rights reserved.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.

    //Add image
    UIImage *uiimage=[UIImage imageNamed:@"patterns.jpg"];
    UIImageView *view=[[UIImageView alloc] initWithImage:uiimage];
    [self.view addSubview:view];
    
    //Add orientation notification
    [[NSNotificationCenter defaultCenter] addObserver:self
                                               selector:@selector(orientationChanged)
                                                 name:UIDeviceOrientationDidChangeNotification
                                               object:nil];
   
    
}

-(void) orientationChanged{
    switch([[UIDevice currentDevice] orientation]){
        case UIDeviceOrientationPortrait:
            NSLog(@">>>Portrait");
            break;
            case UIDeviceOrientationPortraitUpsideDown:
            NSLog(@">>>PortraitUpsideDown");
            break;
        case UIDeviceOrientationLandscapeLeft:
            NSLog(@">>>LandscapeLeft");
            break;
        case UIDeviceOrientationLandscapeRight:
            NSLog(@">>>LandscapeRight");
            break;
        case UIDeviceOrientationFaceDown:
            NSLog(@">>>FaceDown");
            break;
        case UIDeviceOrientationFaceUp:
            NSLog(@">>>FaceUp");
            break;
        case UIDeviceOrientationUnknown:
            NSLog(@">>>Unknown");
            break;
    }
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
