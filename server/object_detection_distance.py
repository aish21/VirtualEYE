import numpy as np
import os
import six.moves.urllib as urllib
import urllib.request as allib
import sys
import tarfile
import tensorflow as tf
import zipfile
import time
import pytesseract
import engineio
import torch
from torch.autograd import Variable as V
import models as models
from torchvision import transforms as trn
from torch.nn import functional as F
import pyttsx3
engine =pyttsx3.init()
from collections import defaultdict
from io import StringIO
from matplotlib import pyplot as plt
from PIL import Image
from models.research.object_detection.utils import label_map_util
from models.research.object_detection.utils import visualization_utils as vis_util

arch = 'resnet18'
model_file = 'whole_%s_places365_python36.pth.tar' % arch
if not os.access(model_file, os.W_OK):
    weight_url = 'http://places2.csail.mit.edu/models_places365/' + model_file
    os.system('wget ' + weight_url)

MODEL_NAME = 'ssd_inception_v2_coco_2017_11_17'
MODEL_FILE = MODEL_NAME + '.tar.gz'
DOWNLOAD_BASE = 'http://download.tensorflow.org/models/object_detection/'

# Path to frozen detection graph. This is the actual model that is used for the object detection.
PATH_TO_CKPT = MODEL_NAME + '/frozen_inference_graph.pb'
PATH_TO_LABELS = os.path.join('data', 'mscoco_label_map.pbtxt')
DOWNLOAD_BASE = 'http://download.tensorflow.org/models'
NUM_CLASSES = 90

# Download Model
if not os.path.exists(MODEL_NAME + '/frozen_inference_graph.pb'):
    print ('Downloading the model')
    opener = urllib.request.URLopener()
    opener.retrieve(DOWNLOAD_BASE + MODEL_FILE, MODEL_FILE)
    tar_file = tarfile.open(MODEL_FILE)
    for file in tar_file.getmembers():
        file_name = os.path.basename(file.name)
        if 'frozen_inference_graph.pb' in file_name:
            tar_file.extract(file, os.getcwd())
            print('Download complete')
else:
	print('Model already exists')
