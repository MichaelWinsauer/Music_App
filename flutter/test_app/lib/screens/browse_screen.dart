import 'dart:io';
import 'dart:typed_data';

import 'package:dart_tags/dart_tags.dart';
import 'package:flutter/material.dart';
import '../widgets/main_drawer.dart';
import '../widgets/mediabar.dart';
import '../widgets/song_item.dart';

class BrowseScreen extends StatefulWidget {
  @override
  _BrowseScreenState createState() => _BrowseScreenState();
}

class _BrowseScreenState extends State<BrowseScreen> {
  static Directory musicDir = Directory('/storage/emulated/0/Music');
  List<FileSystemEntity> musicList = musicDir.listSync();
  String artist = '';
  String title = '';

  void getTags() async {
    TagProcessor tagProcessor = TagProcessor();
    File musicFile = File(musicList[15].path);
    var l = await tagProcessor.getTagsFromByteData(
        ByteData.view(musicFile.readAsBytesSync().buffer), [TagType.id3v2]);

    title = l[0].tags['title'];
    artist = l[0].tags['title'];
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Browse Songs'),
      ),
      drawer: MainDrawer(),
      body: Stack(
        children: <Widget>[
          // ListView(
          //   children: <Widget>[
          //     SongItem('Who Am I', 'Michael Wyckoff'),
          //     SongItem('Mamma Mia', 'Abba'),
          //     SongItem('Money Money Money', 'Abba'),
          //     SongItem('Dragonborn', 'Skyrim OST'),
          //     SongItem('Discord', 'Eurobeat Brony'),
          //     SongItem('Smoke On The Water', 'Deep Purple'),
          //     SongItem('We Will Rock You', 'Queen'),
          //     SongItem('Under Pressure', 'Queen (ft. David Bowie)'),
          //     SongItem('Highway To Hell', 'AC\/DC'),
          //     SongItem('Smells Like Teen Spirit', 'Nirvana'),
          //     SongItem('Wir haben Grund zum Feiern', 'Otto Waalkes'),
          //     SizedBox(height: 130),
          //   ],
          // ),
          ListView.builder(
              itemCount: musicList.length + 1,
              itemBuilder: (ctx, index) {
                if (index < musicList.length) {
                  return SongItem(musicList[index].path, '$index');
                } else {
                  return SizedBox(
                    height: 130,
                    width: double.infinity,
                  );
                }
              }),
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
