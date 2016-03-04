import numpy as np
import time
from scipy import spatial
from sklearn import neighbors
import sys
from sklearn.svm import SVC
from sklearn import svm
import math
sys.setrecursionlimit(100000000)

TestsampleNum = 48217
TrainsampleNum = 72325
trainVector = np.zeros([TrainsampleNum,21]) # 72325 samples in total, each in a 7-component vector format
testVector = np.zeros([TestsampleNum,21]) # 10000 samples in total, each in a 7-component vector format
predictedLabel = np.random.rand(TestsampleNum)

def readFile(filename,sampleNum): # input the file to be processed and Number of samples, intend to output vector format
    vector = np.zeros([sampleNum,21])
    label = np.random.rand(sampleNum)
    file = open(filename)
    print "filename = \n", filename
    print "begin read data\n"
    AllData = np.genfromtxt(file, delimiter=",")
    print "end read data\n"
    file.close()
    print AllData
    #print PreprocessData.shape[0]
    #print PreprocessData.shape[1]
    PreviousId = AllData[0][0]
    index = 0
    for i in range(AllData.shape[0]): # ith row in original input data
        CurrentId = AllData[i][0]
        if CurrentId == PreviousId: # add a new term in the vector
            vector[index][AllData[i][1]] = AllData[i][4]
            vector[index][AllData[i][1] + 7] = AllData[i][5]
            vector[index][AllData[i][1] + 14] = AllData[i][6]
            label[index] = AllData[i][2]
        else:
            index = index + 1 # 1st record for a new sample/enrollment
            vector[index][AllData[i][1]] = AllData[i][4]
            vector[index][AllData[i][1] + 7] = AllData[i][5]
            vector[index][AllData[i][1] + 14] = AllData[i][6]
            label[index] = AllData[i][2]
            PreviousId = CurrentId
    print vector
    print label
    print vector.shape[0]
    print vector.shape[1]
    return vector, label

def constructKDTree():
    tree = neighbors.KDTree(trainVector, leaf_size=2)
    return tree

def KNN(array, k, searchTree): #input the x array (an array of points to query), input k value
   # tree = spatial.KDTree(trainVector)
   #print tree.data
   # print tree.data
    resultset = np.zeros([k, 21])
    resultlabelset = np.zeros(k)
    #tree = spatial.KDTree(trainVector)
    distance, knn_index = searchTree.query(array, k)
    knn_index = knn_index.reshape(k)
    for i in range(len(knn_index)):
        resultset[i,:] = trainVector[knn_index[i],:]
        resultlabelset[i] = trainLabel[knn_index[i]]
    return resultset, resultlabelset# is this correct?

def weight_vector_21(filename):
    file = open(filename)
    data = np.genfromtxt(file, delimiter=",")
    file.close()
    print "data shape = \n", data.shape
    print "data =\n", data

    type_sum = np.zeros(7)
    type_sum1 = np.zeros(7)
    type_sum2 = np.zeros(7)
    for i in range(7):
        type_sum[i] = type_sum[i] + data[i * 2][3]
        type_sum[i] = type_sum[i] + data[i * 2 + 1][3]
        type_sum1[i] = type_sum1[i] + data[i * 2 + 14][3]
        type_sum1[i] = type_sum1[i] + data[i * 2 + 1 + 14][3]
        type_sum2[i] = type_sum2[i] + data[i * 2 + 28][3]
        type_sum2[i] = type_sum2[i] + data[i * 2 + 1 + 28][3]
    type_tol = np.sum(type_sum)
    type_tol1 = np.sum(type_sum1)
    type_tol2 = np.sum(type_sum2)

    print "type_sum = \n", type_sum
    print "type_tol = \n", type_tol
    print "type_sum = \n", type_sum1
    print "type_tol = \n", type_tol1
    print "type_sum = \n", type_sum2
    print "type_tol = \n", type_tol2


    ratio = np.zeros(21)
    for i in range(7):
        if data[i * 2][3] > data[i * 2 + 1][3]:
            ratio[i] = data[i * 2][3] / float(data[i * 2 + 1][3])
            ratio[i] = ratio[i] * (type_sum[i] / float (type_tol))
        else:
            ratio[i] = data[i * 2 + 1][3] / float(data[i * 2][3]) # inner ratio
            ratio[i] = ratio[i] * (type_sum[i] / float (type_tol)) # weight among all counts
            ratio[i] = (-1) * ratio[i] # direction
    for i in range(7):
        if data[i * 2 + 14][3] > data[i * 2 + 1 + 14][3]:
            ratio[i + 7] = data[i * 2 + 14][3] / float(data[i * 2 + 1 + 14][3])
            ratio[i + 7] = ratio[i + 7] * (type_sum1[i] / float (type_tol1))
        else:
            ratio[i + 7] = data[i * 2 + 1 + 14][3] / float(data[i * 2 + 14][3]) # inner ratio
            ratio[i + 7] = ratio[i + 7] * (type_sum1[i] / float (type_tol1)) # weight among all counts
            ratio[i + 7] = (-1) * ratio[i + 7] # direction
    for i in range(7):
        if data[i * 2 + 28][3] > data[i * 2 + 1 + 28][3]:
            ratio[i + 14] = data[i * 2 + 28][3] / float(data[i * 2 + 1 + 28][3])
            ratio[i + 14] = ratio[i + 14] * (type_sum2[i] / float (type_tol2))
        else:
            ratio[i + 14] = data[i * 2 + 1 + 28][3] / float(data[i * 2 + 28][3]) # inner ratio
            ratio[i + 14] = ratio[i + 14] * (type_sum2[i] / float (type_tol2)) # weight among all counts
            ratio[i + 14] = (-1) * ratio[i + 14] # direction
    return ratio

def cal_accuracy(testlabel, predictedlabel):
    correct = 0;
    for i in range(len(testlabel)):
        if testlabel[i] == predictedlabel[i]:
            correct = correct + 1
    accuracy = correct / float(TestsampleNum)
    print "in cal_accuracy TestsampleNum = \n", TestsampleNum
    return accuracy

if __name__ == '__main__':
    startTime = time.time()
    useWeight = int(sys.argv[1]) # Use weight info or not: 1-use; 0-not use
    trainfile = sys.argv[2]
    testfile = sys.argv[3]
    weightfile = sys.argv[4]
    k_neighbor = int(sys.argv[5])
    print "begin read train file\n"
    trainVector, trainLabel = readFile(trainfile, TrainsampleNum)
    print "end read train file\n"
    testVector, testLabel = readFile(testfile, TestsampleNum)
    # print testVector

    if(useWeight != 0):
        ratio = weight_vector_21(weightfile)
        print "ratio = \n", ratio
        for i in range(21):
            trainVector[:,i] =  trainVector[:,i] * math.sqrt(math.fabs(ratio[i])) # -0.12748538
            testVector[:,i] =  testVector[:,i] * math.sqrt(math.fabs(ratio[i])) # -0.12748538
   
    clf = svm.LinearSVC()
    clf.fit(trainVector, trainLabel)
    predictedLabel = clf.predict(testVector)

    print "accuracy = \n", cal_accuracy(testLabel, predictedLabel)
    endTime = time.time()
    period = (endTime - startTime)
    print "period = \n", period
