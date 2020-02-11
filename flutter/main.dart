import 'package:flutter/material.dart';
import './widgets/main_drawer.dart';
import './screens/browse_screen.dart';
import './screens/playlist_screen.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'MusicApp',
      theme: ThemeData(
        primarySwatch: Colors.deepPurple,
      ),
      routes: {
        '/': (ctx) => BrowseScreen(),
        PlaylistScreen.routeName: (ctx) => PlaylistScreen(),
      },
    );
  }
}
