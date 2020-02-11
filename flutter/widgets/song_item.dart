import 'package:flutter/material.dart';

class SongItem extends StatelessWidget {
  final String title;
  final String artist;

  SongItem(
    this.title,
    this.artist,
  );

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 5,
      child: ListTile(
        trailing: Container(
          width: 60,
          child: Row(
            children: <Widget>[
              SizedBox(
                width: 30,
                child: IconButton(
                  icon: Icon(Icons.favorite_border),
                  color: Colors.red,
                  onPressed: () => {},
                ),
              ),
              SizedBox(
                width: 30,
                child: IconButton(
                  icon: Icon(Icons.more_vert),
                  onPressed: () => {},
                ),
              ),
            ],
          ),
        ),
        leading: Container(
          width: 50,
          height: 50,
          color: Colors.black,
          child: Icon(
            Icons.play_arrow,
            color: Colors.white,
          ),
        ),
        title: Text(
          title,
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        subtitle: Text(artist),
      ),
    );
  }
}
