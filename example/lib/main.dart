import 'dart:developer';

import 'package:flutter/material.dart';
import 'dart:async';
import 'package:gromore_ad/flutter_ad_callback.dart';
import 'package:gromore_ad/gromore_ad.dart';
import 'package:gromore_ad/flutter_ad_stream.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: Home(),
    );
  }
}

class Home extends StatefulWidget {
  const Home({Key? key}) : super(key: key);

  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  final String _platformVersion = 'Unknown';
  late Future futureAd;
  late StreamSubscription streamSubscription;
  @override
  void initState() {
    super.initState();
    futureAd = getData();
  }

  getData() {
      streamSubscription =  FlutterAdStream.initAdStream(
          flutterAdRewardAdCallBack:
      FlutterAdRewardAdCallBack(
        onShow: (){

        },
        onClick: (){

        },
        onSkip: (){

        }
    ),flutterAdNewInteractionCallBack: FlutterAdNewInteractionCallBack(
         onShow: (){

         },
          onClick: (){

          },
          onSkip: (){

          }
      ));
    return Future.wait(
        [GromoreAd.register(appId: "5313985", appName: "小飞机", debug: true)]);

  }

 @override
  void dispose() {
    streamSubscription.cancel();
     super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Stack(
          children: [
            FutureBuilder(
              builder: (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
                if (!snapshot.hasData) return const SizedBox();
                GromoreAd.loadAtiveAdInitData(
                  codeId: "102098790",
                  expressViewWidth: 375,
                  expressViewHeight: 326,
                );
                return GromoreAd.splashAdView(
                    codeId: "102095889",
                    forceLoadBottom: true,
                    callBack: FlutterSplashCallBack(onShow: () {
                      log("splashAdView  onShow");
                    }, onFail: (msg) {
                      log("splashAdView  err" + msg.toString());
                    }));
              },
              future: futureAd,
            ),
            Positioned(
                right: 150,
                top: 50,
                child: ElevatedButton(
                    child: Text(_platformVersion), onPressed: () {})),
            Positioned(
              right: 20,
              child: ElevatedButton(
                child: const Text("激励"),
                onPressed: () {
                  GromoreAd.showRewardVideoAd(
                      codeId: "102093632",
                      rewardName: "小飞机激励",
                      rewardAmount: 1,
                      userID: "123");
                },
              ),
            ),
            Positioned(
              left: 50,
              child: ElevatedButton(
                child: const Text("插全屏"),
                onPressed: () {
                  GromoreAd.showFullScreenVideoAdInteraction(
                      codeId: "102098873",
                      rewardName: "小飞机激励",
                      rewardAmount: 1,
                      userID: "123");
                },
              ),
            ),

            Positioned(
              right: 150,
              bottom: 150,
              child: ElevatedButton(
                child: const Text("跳转banner"),
                onPressed: () {

                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => const BannerAdView(),
                    ),
                  );
                },
              ),
            ),

            Align(
              alignment: Alignment.center,
              child: ElevatedButton(
                child: const Text("跳信息流"),
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => const FeedListView(),
                    ),
                  );
                },
              ),
            )
          ],
        ));
  }
}

class FeedListView extends StatefulWidget {
  const FeedListView({Key? key}) : super(key: key);

  @override
  State<FeedListView> createState() => _FeedListViewState();
}

class _FeedListViewState extends State<FeedListView> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(),
        body: ListView.builder(
          itemBuilder: (BuildContext context, int index) {
            return GromoreAd.nativeAdView(
                codeId: "102098790",
                expressViewWidth: 375,
                expressViewHeight: 326,
                adCount: 1);
          },
          itemCount: 30,
        ),
      ),
    );
  }
}

class BannerAdView extends StatelessWidget {
  const BannerAdView({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        home: Scaffold(
        appBar: AppBar(),
          body: ListView.builder(
            itemBuilder: (BuildContext context, int index) {
              return GromoreAd.bannerAdView(
                  codeId: "102104298",
                  expressViewWidth: 375,
                  expressViewHeight: 185,
              );
            },
            itemCount: 30,
          ),
     )
    );
  }
}

