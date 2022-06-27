import 'dart:developer' as log;
import 'dart:io';
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_native_contact/SZWUtils.dart';
import 'package:flutter_native_contact/api_dio.dart';
import 'package:flutter_native_contact/bean/back_log_bean_entity.dart';
import 'package:lottie/lottie.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  //沉浸式状态栏
  if (Platform.isAndroid) {
    SystemChrome.setSystemUIOverlayStyle(const SystemUiOverlayStyle(
      statusBarColor: Colors.transparent,
      systemNavigationBarColor: Color(0xFF000000),
      systemNavigationBarIconBrightness: Brightness.light,
      statusBarIconBrightness: Brightness.dark,
      statusBarBrightness: Brightness.light,
    ));
  }
  var channel = const MethodChannel("com.flutter.guide.MethodChannel");
  channel.setMethodCallHandler((call) {
    SZWUtils().saveData("X-Access-Token", call.arguments);
    return Future(() {});
  });
  runApp(
    const MyApp(),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      routes: {
        '/': (context) => const MyHomePage(
              title: 'aaa',
            ),
      },
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  void initState() {
    super.initState();
    getListData();
  }

  List<BackLogBeanEntity> data = [];

  getListData() async {
    Map<String, dynamic>? param = {};
    APIDio.post<List<BackLogBeanEntity>>(
        url: "/ygxt/index/toDo",
        parameters: param,
        onSuccess: (result) {
          if (result != null) {
            setState(() {
              data = result;
            });
          }
        },
        onError: (result) {
          log.log(result ?? "没有错误");
        }
        // queryParameters: {'name': 'laomeng', 'page': 1}
        );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Stack(
      fit: StackFit.loose,
      children: <Widget>[
        //着色器，为了背景底部渐变色
        ShaderMask(
          shaderCallback: (Rect bounds) {
            return const LinearGradient(
              stops: [0.3, 0.7],
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
              colors: <Color>[
                Colors.white,
                Colors.transparent,
              ],
            ).createShader(bounds);
          },
          blendMode: BlendMode.dstIn,
          child: SizedBox(
            height: 550,
            child: Lottie.asset('assets/json/backgroundorange.json',
                fit: BoxFit.fill),
          ),
        ),
        Card(
            margin: const EdgeInsets.fromLTRB(8.0, 150, 8, 8),
            elevation: 8,
            //裁剪圆角
            clipBehavior: Clip.antiAliasWithSaveLayer,
            child: ListView.builder(
                physics: const BouncingScrollPhysics(),
                //没有appbar时会默认20padding ,去掉
                padding: const EdgeInsets.all(0),
                itemCount: data.length,
                itemBuilder: (BuildContext context, int index) {
                  return Container(
                    height: 50,
                    color: Colors.primaries[Random().nextInt(18)],
                    child: Center(child: Text(data[index].nAME)),
                  );
                })),
      ],
    ));
  }
}
