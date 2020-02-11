import 'package:flutter/material.dart';

class MainDrawer extends StatefulWidget {
  @override
  _MainDrawerState createState() => _MainDrawerState();
}

class _MainDrawerState extends State<MainDrawer> {
  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Drawer(
        child: Column(
          children: <Widget>[
            Container(
              child: Text(
                'Lyritic',
                style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
              ),
              alignment: Alignment.centerLeft,
              padding: EdgeInsets.all(20),
              width: double.infinity,
            ),
            Divider(color: Colors.grey),
            ListTile(
              leading: Icon(Icons.search),
              title: Text('Browse'),
              onTap: () => Navigator.of(context).pushReplacementNamed('/'),
            ),
            ListTile(
              leading: Icon(Icons.featured_play_list),
              title: Text('Playlists'),
              onTap: () => Navigator.of(context).pushReplacementNamed('/playlist_screen'),
            ),
          ],
        ),
      ),
    );
  }
}
