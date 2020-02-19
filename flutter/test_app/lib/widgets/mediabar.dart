import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import 'package:dart_tags/dart_tags.dart';
import 'package:flutter/material.dart';
import 'package:audioplayers/audioplayers.dart';
import 'package:audioplayers/audio_cache.dart';
import 'package:path_provider/path_provider.dart';
import 'package:provider/provider.dart';

import '../providers/song_provider.dart';

class MediaBar extends StatefulWidget {
  static String songPath;
  static AudioPlayer audioPlayer = AudioPlayer();
  static bool isPlaying = false;

  // void setSongPath(String path) {
  //   songPath = path;
  //   stopSong();
  // }

  static void stopSong() {
    audioPlayer.stop();
    isPlaying = false;
  }

  static void pauseSong() {
    audioPlayer.pause();
    isPlaying = false;
  }

  static void playSong() async{
    int result = await MediaBar.audioPlayer.play(Song.path, isLocal: true);
    if (result == 1) {
      isPlaying = true;
    }
  }

  @override
  _MediaBarState createState() => _MediaBarState();
}

class _MediaBarState extends State<MediaBar> {
  double sliderValue = 0;

  void playAudioFile() async {
    int result = await MediaBar.audioPlayer.play(Song.path, isLocal: true);
    if (result == 1) {
      // success
    }

    setState(() {
      MediaBar.isPlaying = true;
    });

    // TagProcessor tagProcessor = TagProcessor();
    // File musicFile = File(musicList[15].path);
    // var l = await tagProcessor.getTagsFromByteData(
    //     ByteData.view(musicFile.readAsBytesSync().buffer), [TagType.id3v2]);

    // print(l[0].tags['artist']);
    // print(l[0].tags['title']);
  }

  String get _localPath {
    return '/storage/emulated/0/Music/';
  }

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    MediaBar.isPlaying = false;
  }

  @override
  Widget build(BuildContext context) {
    final song = Provider.of<Song>(context);
    return Container(
      color: Colors.black87,
      width: double.infinity,
      height: 130,
      child: Column(
        children: <Widget>[
          Container(
            width: double.infinity,
            child: Row(
              children: <Widget>[
                Flexible(
                  flex: 1,
                  fit: FlexFit.tight,
                  child: Center(
                    child: Text(
                      '0:00',
                      style: TextStyle(color: Colors.white),
                    ),
                  ),
                ),
                Flexible(
                  flex: 6,
                  fit: FlexFit.tight,
                  child: Slider(
                    value: sliderValue,
                    onChanged: (sliderValue) => {},
                  ),
                ),
                Flexible(
                  flex: 1,
                  fit: FlexFit.tight,
                  child: Center(
                    child: Text(
                      '3:00',
                      style: TextStyle(color: Colors.white),
                    ),
                  ),
                ),
              ],
            ),
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: <Widget>[
              Flexible(
                flex: 1,
                fit: FlexFit.tight,
                child: RawMaterialButton(
                  onPressed: () {},
                  child: new Icon(
                    Icons.skip_previous,
                    color: Colors.white,
                    size: 20.0,
                  ),
                  shape: new CircleBorder(),
                  elevation: 2.0,
                  fillColor: Theme.of(context).primaryColor,
                  padding: const EdgeInsets.all(15.0),
                ),
              ),
              Flexible(
                flex: 1,
                fit: FlexFit.tight,
                child: RawMaterialButton(
                  onPressed: () {
                    if (MediaBar.isPlaying) {
                      setState(() {
                        MediaBar.pauseSong();
                      });
                    } else {
                      setState(() {
                        MediaBar.playSong();
                      });
                    }
                  },
                  child: new Icon(
                    !MediaBar.isPlaying ? Icons.play_arrow : Icons.pause,
                    color: Colors.white,
                    size: 35.0,
                  ),
                  shape: new CircleBorder(),
                  elevation: 2.0,
                  fillColor: Theme.of(context).primaryColor,
                  padding: const EdgeInsets.all(15.0),
                ),
              ),
              Flexible(
                flex: 1,
                fit: FlexFit.tight,
                child: RawMaterialButton(
                  onPressed: () {},
                  child: new Icon(
                    Icons.skip_next,
                    color: Colors.white,
                    size: 20.0,
                  ),
                  shape: new CircleBorder(),
                  elevation: 2.0,
                  fillColor: Theme.of(context).primaryColor,
                  padding: const EdgeInsets.all(15.0),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
