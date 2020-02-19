import 'package:flutter/material.dart';

class Song with ChangeNotifier {
  String title;
  String artist;
  static String path = '';


  void changePath(String newPath) {
    path = newPath;
    notifyListeners();
  }
}
