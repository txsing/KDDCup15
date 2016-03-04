import numpy as np
import time
from sklearn import neighbors
import sys
from sklearn.svm import SVC
sys.setrecursionlimit(100000000)

TestsampleNum = 48217
trainVector = np.zeros([72325,7]) # 72325 samples in total, each in a 7-component vector format
trainLabel = np.random.rand(72325)
testVector = np.zeros([TestsampleNum,7]) # 10000 samples in total, each in a 7-component vector format
testLabel = np.random.rand(TestsampleNum)
predictedLabel = np.random.rand(TestsampleNum)

trainFile = "WOT_Train_Vector.csv"
def readTrainFile(filename): # input the file to be processed
    file = open(filename)
    PreprocessData = np.genfromtxt(file, delimiter=",")
    file.close()
    print PreprocessData
    #print PreprocessData.shape[0]
    #print PreprocessData.shape[1]
    return PreprocessData


def constructKDTree():
    tree = neighbors.KDTree(trainVector, leaf_size=2)
    return tree

def KNN(array, k, searchTree): #input the x array (an array of points to query), input k value
   # tree = spatial.KDTree(trainVector)
   #print tree.data
   # print tree.data
    resultset = np.zeros([k, 7])
    resultlabelset = np.zeros(k)
    #tree = spatial.KDTree(trainVector)
    distance, knn_index = searchTree.query(array, k)
    knn_index = knn_index.reshape(k)
    for i in range(len(knn_index)):
        resultset[i,:] = trainVector[knn_index[i],:]
        resultlabelset[i] = trainLabel[knn_index[i]]
    return resultset, resultlabelset# is this correct?

def weight_vector(filename):
    file = open(filename)
    data = np.genfromtxt(file, delimiter=",")
    file.close()
    print "data shape = \n", data.shape
    print "data =\n", data

    type_sum = np.zeros(7)
    for i in range(7):
        type_sum[i] = type_sum[i] + data[i * 2][2]
        type_sum[i] = type_sum[i] + data[i * 2 + 1][2]
    type_tol = np.sum(type_sum)

    print "type_sum = \n", type_sum
    print "type_tol = \n", type_tol


    ratio = np.zeros(7)
    for i in range(7):
        if data[i * 2][2] > data[i * 2 + 1][2]:
            ratio[i] = data[i * 2][2] / float(data[i * 2 + 1][2])
            ratio[i] = ratio[i] * (type_sum[i] / float (type_tol))
        else:
            ratio[i] = data[i * 2 + 1][2] / float(data[i * 2][2]) # inner ratio
            ratio[i] = ratio[i] * (type_sum[i] / float (type_tol)) # weight among all counts
            ratio[i] = (-1) * ratio[i] # direction
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
    AllData = readTrainFile(trainfile)
    PreviousId = AllData[0][0]
    index = 0
    for i in range(AllData.shape[0]): # ith row in original input data
        CurrentId = AllData[i][0]
        if CurrentId == PreviousId: # add a new term in the vector
            trainVector[index][AllData[i][1]] = AllData[i][2]
            trainLabel[index] = AllData[i][3]
        else:
            index = index + 1 # 1st record for a new sample/enrollment
            trainVector[index][AllData[i][1]] = AllData[i][2]
            trainLabel[index] = AllData[i][3]
            PreviousId = CurrentId
    # print trainVector
    print trainVector.shape[0]
    print trainVector.shape[1]

    TestData = readTrainFile(testfile)
    PreviousId = TestData[0][0]
    index = 0
    # for i in range(TestData.shape[0]): # ith row in original input data
    for i in range(TestData.shape[0]): # ith row in original input data
        CurrentId = TestData[i][0]
        if CurrentId == PreviousId: # add a new term in the vector
            testVector[index][TestData[i][1]] = TestData[i][2]
            testLabel[index] = TestData[i][3]
        else:
            index = index + 1 # 1st record for a new sample/enrollment
            testVector[index][TestData[i][1]] = TestData[i][2]
            testLabel[index] = TestData[i][3]
            PreviousId = CurrentId
    # print testVector
    print testVector.shape[0]
    print testVector.shape[1]

    print "TestsampleNum = \n", TestsampleNum
    if(useWeight != 0):
        ratio = weight_vector(weightfile)
        print "ratio = \n", ratio
        trainVector[:,0] =  trainVector[:,0] * ratio[0] # -0.12748538
        trainVector[:,1] =  trainVector[:,1] * ratio[1] # 0.57295446
        trainVector[:,2] =  trainVector[:,2] * ratio[2] # 0.27336366
        trainVector[:,3] =  trainVector[:,3] * ratio[3] # 0.22940873
        trainVector[:,4] =  trainVector[:,4] * ratio[4] # 0.01308849
        trainVector[:,5] =  trainVector[:,5] * ratio[5] # 0.13693268
        trainVector[:,6] =  trainVector[:,6] * ratio[6] # 0.18386892
        testVector[:,0] =  testVector[:,0] * ratio[0] # -0.12748538
        testVector[:,1] =  testVector[:,1] * ratio[1] # 0.57295446
        testVector[:,2] =  testVector[:,2] * ratio[2] # 0.27336366
        testVector[:,3] =  testVector[:,3] * ratio[3] # 0.22940873
        testVector[:,4] =  testVector[:,4] * ratio[4] # 0.01308849
        testVector[:,5] =  testVector[:,5] * ratio[5] # 0.13693268
        testVector[:,6] =  testVector[:,6] * ratio[6] # 0.18386892
    # print testVector
   
    tree = constructKDTree()
    print "KNN neighbour\n"
    correct_num = 0
    for i in range(TestsampleNum):
        if i % 1000 == 0:
            print "processing i = \n", i

        neighbours, neighbours_label = KNN(testVector[i,:],k_neighbor,tree)

        label_1_num = 0
        for j in range(len(neighbours_label)): # can not use i!!
            if neighbours_label[j] == 1:
                label_1_num = label_1_num + 1

        if label_1_num > (len(neighbours_label) - label_1_num):
            predictedLabel[i] = 1
        else:
            predictedLabel[i] = 0
 
        if predictedLabel[i] == testLabel[i]:
	       correct_num = correct_num + 1

        if i % 1000 == 0 and i != 0:
            print "predicted label = \n", predictedLabel[i]
            print "true label = \n", testLabel[i]
            print "correct_num = \n", correct_num
            print "accuracy = \n", correct_num / float(i)

    print "accuracy = \n", cal_accuracy(testLabel, predictedLabel)
    endTime = time.time()
    period = endTime - startTime
    print "period = \n", period
