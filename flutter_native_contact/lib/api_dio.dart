import 'dart:convert' as convert;
import 'dart:convert';
import 'dart:developer';
import 'dart:io';

import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_native_contact/SZWUtils.dart';
import 'package:flutter_native_contact/utils/my_date_utils.dart';
import 'package:flutter_native_contact/utils/net/net_entity.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:package_info/package_info.dart';

import 'generated/json/base/json_convert_content.dart';

class APIDio {
  // 工厂模式
  // late  static Dio dio
  static Dio? dio;

  static const int CONNECT_TIMEOUT = 10000;
  static const int RECEIVE_TIMEOUT = 10000;

  static const String GET = 'get';
  static const String POST = 'post';
  static const String PUT = 'put';
  static const String PATCH = 'patch';
  static const String DELETE = 'delete';

  static Dio createInstance(Map<String, dynamic> header) {
    //仅仅初始化一次。
    if (dio == null) {
      //设置请求参数 基础配置
      late int requestTime;
      late int revicerTime;
      int sRequestId = 0;
      dio = Dio();
      dio?.options = BaseOptions(
          baseUrl: "http://192.168.3.31:8010/jeecg-boot/",
          connectTimeout: CONNECT_TIMEOUT,
          receiveTimeout: RECEIVE_TIMEOUT,
          headers:  getHeader(header) );
      //设置拦截器 打印日志等
      dio?.interceptors.add(InterceptorsWrapper(onRequest: (options, handler) {
        requestTime = DateTime.now().millisecondsSinceEpoch;
        sRequestId++;
        handler.next(options);
        return;
      }, onResponse: (response, handler) async {
        _printLog(response, sRequestId, requestTime);
        // var resDataList = convert.jsonDecode(response.data);
        var resDataList = response.data;
        int code = resDataList['code'];
        if (code == 0 || code == 1) {
          handler.next(response);
          return;
          //-200 token失效
        } else if (code == -200) {
          //当出现错误 把 msg 打出来
          // ToastUtils.showToast("登录信息过期。请重新登录");
          Fluttertoast.showToast(
              msg: "This is Center Short Toast",
              toastLength: Toast.LENGTH_SHORT,
              gravity: ToastGravity.CENTER,
              timeInSecForIosWeb: 1,
              backgroundColor: Colors.red,
              textColor: Colors.white,
              fontSize: 16.0);
          // PassportManager.logout();
          // PassportManager.goLogin(KeyUtils.globalKey.currentState!.context)
          //     .then((value) => null);
        } else {
          //-1是返回错误
          // // ToastUtils.showToast(resDataList['msg']);
          //主动抛出异常
          // return handler.reject(DioError(
          //     error: resDataList['msg'],
          //     requestOptions: response.requestOptions));
          handler.next(response);
          return;
        }
      }, onError: (DioError e, handler) {
        return handler.next(e);
      }));
    }
    return dio!;
  }

// https://blog.csdn.net/zhayunbiao/article/details/109361229 网络请求优化
  static Future<void> _printLog(
      Response response, int sRequestId, int requestTime) async {
    int revicerTime = DateTime.now().millisecondsSinceEpoch;
    // print('***************** ${response.requestOptions.uri} *****************');
    // print('data： ${response.requestOptions.data}');
    // printMoreLineLog(response.toString());
    sRequestId++;
    String inputContent = "网络日志：\n" +
        "--> sending request ${response.requestOptions.uri.toString()} \n" +
        "method: ${response.requestOptions.method}\n" +
        "sequence: ${sRequestId.toString()}\n" +
        "requestTime: ${MyDateUtils.formatDateMilliseconds(requestTime, format: "yyyy-MM-dd HH:mm:ss SSS")}\n" +
        "params: ${response.requestOptions.queryParameters.toString()}\n" +
        "data: ${response.requestOptions.data.toString()}\n" +
        "header: \n{\n${response.requestOptions.headers.toString()}}\n" +
        "<--received response for ${response.requestOptions.uri.toString()}\n" +
        "sequence:  ${sRequestId.toString()}\n" +
        "receivedTime: ${MyDateUtils.formatDateMilliseconds(revicerTime, format: "yyyy-MM-dd HH:mm:ss SSS")}\n" +
        "duration: ${(revicerTime - requestTime).toString()}\n" +
        "response: ${response.toString()}\n" +
        "header: \n{\n${response.headers.map.toString()}\n";
    //直接运行
    if (kDebugMode || kProfileMode) {
      // LogUtil.d(inputContent);
      //测试环境下打印日志
      log(inputContent);
    }
  }
  static var fromPlatform =  PackageInfo.fromPlatform();
  static  getHeader(Map<String, dynamic> header)  {

    var  hearder  = {
      'Content-Type': 'application/x-www-form-urlencode',
      'X-Access-Token':header["token"],
      'AppVersion':header["version"] ,
      'channel': 'app_${Platform.isAndroid ? "android" : "ios"}',
    };
    return hearder ;
  }

  static Future _getResponse<T>(
      {required String url,
      required String method,
      parameters,
      Function(T? t)? onSuccess,
      Function(String? error)? onError}) async {
    // try {
    Response response;
    var packageInfo = await PackageInfo.fromPlatform();
    var token = await SZWUtils().readData("X-Access-Token");
    var header={
        "token":token,
        "version":packageInfo.version
    };
    Dio dio = createInstance(header);
    switch (method) {
      case GET:
        response = await dio.get(url, queryParameters: parameters);
        break;
      case PUT:
        response = await dio.put(url, queryParameters: parameters);
        break;
      case PATCH:
        response = await dio.patch(url, queryParameters: parameters);
        break;
      case DELETE:
        response = await dio.delete(url, queryParameters: parameters);
        break;
      default:
        response = await dio.post(url, data: parameters);
        break;

    }

    /// 拦截http层异常码
    if (response.statusCode == 200) {
      /// 这里做baseBena泛型解析，封装 并拦截后台code异常码，可拦截自定义处理
      // Map<String, dynamic> jsonData = jsonDecode(response.data);
      Map<String, dynamic> jsonData = response.data;

      var bean = NetEntity<T>();
      bean.msg = jsonData['msg'];
      bean.code = jsonData['code'];
      if (jsonData.containsKey("result")) {
        bean.result = JsonConvert.fromJsonAsT<T>(jsonData['result']);
      }
      if (response.statusCode == 200 && onSuccess != null) {
        if (bean.code == 200 && onSuccess != null) {
          onSuccess(bean.result);
        } else {
          if (onError != null) {
            //自定义网络请求错误
            onError(bean.msg);
          }
        }
      } else {
        if (onError != null) {
          onError(bean.msg);
        }
      }
    } else {
      throw Exception(
          'statusCode:${response.statusCode}+${response.statusMessage}');
    }
    // } catch (e) {
    //   print('请求出错：' + e.toString());
    //   if (onError != null) {
    //     onError(e.toString());
    //   }
    // }
  }

  static Future<dynamic> get<T>(
      {required String url,
      required parameters,
      Function(T? t)? onSuccess,
      Function(String? error)? onError}) async {
    // NetWorkUtils.connectTips();
    await _getResponse<T>(
        url: url,
        method: GET,
        parameters: parameters,
        onSuccess: onSuccess,
        onError: onError);
  }

  static Future<dynamic> post<T>(
      {required String url,
      required parameters,
      Function(T? t)? onSuccess,
      Function(String? error)? onError}) async {
    // NetWorkUtils.connectTips();
    await _getResponse<T>(
        url: url,
        method: POST,
        parameters: parameters,
        onSuccess: onSuccess,
        onError: onError);
  }

  static void handleErr(DioError e) {
    if (kDebugMode) {
      print(
        "DioError:${e.requestOptions.uri} ${e.message}",
      );
    }
    if (e.message.isEmpty) return;

    throw '$e';
  }
}
