import 'package:flutter_native_contact/generated/json/base/json_convert_content.dart';
import 'package:flutter_native_contact/bean/back_log_bean_entity.dart';

BackLogBeanEntity $BackLogBeanEntityFromJson(Map<String, dynamic> json) {
	final BackLogBeanEntity backLogBeanEntity = BackLogBeanEntity();
	final String? aNDROID = jsonConvert.convert<String>(json['ANDROID']);
	if (aNDROID != null) {
		backLogBeanEntity.aNDROID = aNDROID;
	}
	final String? nAME = jsonConvert.convert<String>(json['NAME']);
	if (nAME != null) {
		backLogBeanEntity.nAME = nAME;
	}
	final String? nUM = jsonConvert.convert<String>(json['NUM']);
	if (nUM != null) {
		backLogBeanEntity.nUM = nUM;
	}
	return backLogBeanEntity;
}

Map<String, dynamic> $BackLogBeanEntityToJson(BackLogBeanEntity entity) {
	final Map<String, dynamic> data = <String, dynamic>{};
	data['ANDROID'] = entity.aNDROID;
	data['NAME'] = entity.nAME;
	data['NUM'] = entity.nUM;
	return data;
}