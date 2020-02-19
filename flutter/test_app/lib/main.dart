import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import './widgets/main_drawer.dart';
import './screens/browse_screen.dart';
import './screens/playlist_screen.dart';

import './providers/song_provider.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider.value(
          value: Song(),
        ),
      ],
      child: MaterialApp(
        title: 'MusicApp',
        theme: ThemeData(
          primarySwatch: Colors.deepPurple,
        ),
        routes: {
          '/': (ctx) => BrowseScreen(),
          PlaylistScreen.routeName: (ctx) => PlaylistScreen(),
        },
      ),
    );
  }
}
