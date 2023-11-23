# Melodex: Identify Songs with Ease

## Overview
MeloDex is an android app that functions by capturing and analyzing internal audio from various applications such as Instagram reels and YouTube shorts. 
It effectively identifies the audio being played and offers convenient links to Spotify, YouTube, and the web for further exploration.

## Features
- **Song Recognition:** Identify songs by capturing audio samples.
- **Integration:** Utilizes the ACR Cloud API for accurate music recognition.
- **Service:** Runs a foreground service for continuous song identification.
- **Quick Links:** Provides a quick link to search for recognized songs on Google, Youtube, and Spotify.
- **User Interface:** Simple and intuitive interface for easy interaction.

## Usage

1. **Grant Permissions:** Ensure Melodex has the necessary permissions for audio and overlay access on your device.
2. **Initiate Song Recognition:**
   - Tap the "Allow Casting" button to start the song recognition process.
   - Play the reel/short on Instagram or YouTube.
3. **Capture Audio Samples:**
   - Click on the play button to allow Melodex to capture audio samples.
   - Let the audio play for a few seconds.
4. **Identify the Song:**
   - Click on the pause button to stop capturing audio and identify the song.
5. **Explore Recognized Song Details:**
   - Once the song is identified, view it on Google, Spotify, or YouTube using the provided links, wherever it is available.
app/src/main
## Architecture Overview
![Alt text](app/src/main/ACR_ARCH_melodex.png)
