import cv2

filename = 'face.jpg'

def detect(filename):
    face_cascade = cv2.CascadeClassifier('./cascodes/haarcascade_frontalface_default.xml')

    img = cv2.imread(filename)

    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    faces = face_cascade.detectMultiScale(gray, 1.3, 5)
    for (x, y, w, h) in faces:
        print faces
        img = cv2.rectangle(img, (x, y), (x + w + 2, y + h + 2), (255, 255, 0), 2)

    cv2.namedWindow('facedetect')
    cv2.imshow('facedetect', img)
    # cv2.imwrite('./detected.jpg', img)
    cv2.waitKey()
    cv2.destroyAllWindows()

detect(filename)