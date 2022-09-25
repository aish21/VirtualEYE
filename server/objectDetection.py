''' Python Script for simple object detection tests '''

import os
from imageai.Detection import ObjectDetection

execPath = os.getcwd()

detector = ObjectDetection()
detector.setModelTypeAsYOLOv3()
