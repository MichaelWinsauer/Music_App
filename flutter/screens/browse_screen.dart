import 'package:flutter/material.dart';
import '../widgets/main_drawer.dart';
import '../widgets/mediabar.dart';
import '../widgets/song_item.dart';

class BrowseScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Browse Songs'),
      ),
      drawer: MainDrawer(),
      body: Stack(
        children: <Widget>[
          ListView(
            children: <Widget>[
              SongItem('Who Am I', 'Michael Wyckoff'),
              SongItem('Mamma Mia', 'Abba'),
              SongItem('Money Money Money', 'Abba'),
              SongItem('Dragonborn', 'Skyrim OST'),
              SongItem('Discord', 'Eurobeat Brony'),
              SongItem('Smoke On The Water', 'Deep Purple'),
              SongItem('We Will Rock You', 'Queen'),
              SongItem('Under Pressure', 'Queen (ft. David Bowie)'),
              SongItem('Highway To Hell', 'AC\/DC'),
              SongItem('Smells Like Teen Spirit', 'Nirvana'),
              SongItem('Wir haben Grund zum Feiern', 'Otto Waalkes'),
              SizedBox(height: 130),
            ],
          ),
          Column(
            children: <Widget>[
              Expanded(child: Container()),
              MediaBar(),
            ],
          )
        ],
      ),
    );
  }
}
