import cv2
import numpy as np

img = cv2.imread('chess_board.png')
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
gray = np.float32(gray)

dst = cv2.cornerHarris(gray, 2, 23, 0.04)
img[dst>0.01 * dst.max()] = [0, 0, 255]
cv2.imwrite('./harri_chess.png', img)
cv2.destroyAllWindows()