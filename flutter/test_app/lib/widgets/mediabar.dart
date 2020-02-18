import 'dart:io';

import 'package:flutter/material.dart';
import 'package:audioplayers/audioplayers.dart';
import 'package:audioplayers/audio_cache.dart';
import 'package:path_provider/path_provider.dart';

class MediaBar extends StatefulWidget {
  @override
  _MediaBarState createState() => _MediaBarState();
}

class _MediaBarState extends State<MediaBar> {
  double sliderValue = 0;
  AudioPlayer audioPlayer = AudioPlayer();
  static AudioCache assetPlayer = AudioCache(prefix: 'music/');

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
  }

  void playAudioFile() async {
    int result = await audioPlayer.play('/storage/emulated/0/Music/AUD-20200207-WA0018.mp3', isLocal: true);
    if (result == 1) {
      // success
    }
    //debugPrint(await _localFile);
  }

  String get _localPath {
    return '/storage/emulated/0/Music/';
  }

  // Future<String> get _localFile async {
  //   final path = await _localPath;
  //   return '$path/Music/AUD-20200207-WA0018.mp3';
  // }

  @override
  Widget build(BuildContext context) {
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
                  onPressed: playAudioFile,
                  child: new Icon(
                    Icons.play_arrow,
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
