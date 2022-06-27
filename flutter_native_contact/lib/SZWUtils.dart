import 'package:shared_preferences/shared_preferences.dart';

class SZWUtils {
     saveData(String key, String value) async {
    var prefs = await SharedPreferences.getInstance();
    prefs.setString(key, value);
  }

  Future<String>  readData(String key) async {
    var prefs = await SharedPreferences.getInstance();
    var result = prefs.getString(key);
    return result ?? '';
  }
}
