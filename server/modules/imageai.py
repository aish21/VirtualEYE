import cv2

# read the image
img = cv2.imread('test2.jpg')

# convert the image to grayscale
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

# threshold the image to identify the black areas
_, threshold = cv2.threshold(gray, 160, 255, cv2.THRESH_BINARY)

# find all the contours in the thresholded image
contours, _ = cv2.findContours(threshold, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

# initialize a variable to store the total area of the black areas
black_area = 0

# loop through the contours and calculate the area of each black area
for cnt in contours:
    area = cv2.contourArea(cnt)
    black_area += area

# calculate the total area of the image
total_area = img.shape[0] * img.shape[1]

# calculate the percentage of black area in the entire image
percent_black = (black_area / total_area) * 100

# print the percentage of black area in the entire image
print("Percentage of Black Area: {:.2f}%".format(percent_black))

# loop through the contours and draw rectangles around the black areas
for cnt in contours:
    x, y, w, h = cv2.boundingRect(cnt)
    cv2.rectangle(img, (x, y), (x + w, y + h), (0, 0, 0), 2)

# display the original image with the black areas highlighted
cv2.imshow("Black Areas", img)
cv2.waitKey(0)
cv2.destroyAllWindows()
