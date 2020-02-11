import 'package:flutter/material.dart';
import '../widgets/main_drawer.dart';

class PlaylistScreen extends StatelessWidget {
  static const String routeName = '/playlist_screen';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Playlists'),
      ),
      drawer: MainDrawer(),
    );
  }
}
