package com.abood.third_proto.w04.client;

/** Everything a renderer needs to draw one frame of the effect. */
record TunnelFrame(TunnelHole hole, float intensity, float timeSeconds, int guiWidth, int guiHeight) {}
