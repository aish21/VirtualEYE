''' Python Script for simple object detection tests '''

from imageai.Detection import ObjectDetection

detector = ObjectDetection()

model_path = "./models/resnet.h5"
input_path = "./input/test2.jpeg"
output_path = "./output/newimage_resnet.jpg"

detector.setModelTypeAsRetinaNet()
detector.setModelPath(model_path)
detector.loadModel()

detection = detector.detectObjectsFromImage(input_image=input_path, output_image_path=output_path)
for eachItem in detection:
    print(eachItem["name"] , " : ", eachItem["percentage_probability"])