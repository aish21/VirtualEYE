''' Python Script for simple object detection tests '''

from imageai.Detection import ObjectDetection
from PIL import Image
from numpy import asarray

detector = ObjectDetection()

model_path = "./models/yolo.h5"
# input_path = "./input/test2.jpeg"
output_path = "./output/np_arr.jpg"

img = Image.open('./input/test2.jpeg')
numpydata = asarray(img)

detector.setModelTypeAsYOLOv3()
detector.setModelPath(model_path)
detector.loadModel(detection_speed="fast")

detection = detector.detectObjectsFromImage(input_type="array", input_image=numpydata, output_image_path=output_path, minimum_percentage_probability=75)
for eachItem in detection:
    print(eachItem["name"] , " : ", eachItem["percentage_probability"])