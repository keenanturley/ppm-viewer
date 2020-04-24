# ppm-viewer
Simple [PPM](https://en.wikipedia.org/wiki/Netpbm) Image Viewer in JavaFX 

This is a small utility I made in preparation for the [Ray Tracng in One Weekend](https://raytracing.github.io/) course.

I couldn't find an easy way to view PPM files on Windows 10, so I figured I would try learning some JavaFX and create my
own viewer. 

It's super simple and I don't plan on doing anything crazy with it. This is mostly an educational exercise.

With that being said, if you have the patience to explain something I'm doing poorly, I would appreciate the feedback.

## Features

Implemented:

- Supports opening ASCII Portable PixMap (P3 .ppm) images files
  - Does not support comments
  - Color max value must be 255
- Open files via `File` > `Open...` menu item (file picker)
- Open files via drag-and-drop
- Updates image automatically when file is changed on disk

Planned / Might Implement Later:

- Provide image path as command line argument
- Center image
- Zooming / scaling
- Panning
