import 'dart:convert';
import 'package:flutter_native_contact/generated/json/base/json_field.dart';
import 'package:flutter_native_contact/generated/json/back_log_bean_entity.g.dart';

@JsonSerializable()
class BackLogBeanEntity {

	@JSONField(name: "ANDROID")
	late String aNDROID;
	@JSONField(name: "NAME")
	late String nAME;
	@JSONField(name: "NUM")
	late String nUM;
  
  BackLogBeanEntity();

  factory BackLogBeanEntity.fromJson(Map<String, dynamic> json) => $BackLogBeanEntityFromJson(json);

  Map<String, dynamic> toJson() => $BackLogBeanEntityToJson(this);

  @override
  String toString() {
    return jsonEncode(this);
  }
}