import json
import os
from matplotlib import pyplot as plt
filename = "output/pathAndCost.json"
with open(filename) as file:
    list = json.load(file)
vehicles = list['vehicles']
totalCost = 0
singleTotalCost = 0
x = []
y = []
i = 0
for vehicle in vehicles:
    for cost in vehicle['cost']:
        singleTotalCost=singleTotalCost+cost
        totalCost = totalCost +cost
    x.append("vehicle"+str(i))
    i=i+1
    y.append(singleTotalCost)
    singleTotalCost = 0
x.append("total delay")
y.append(totalCost)
plt.bar(x, y)
for a,b in zip(x,y):
    plt.text(a, b, b,ha= 'center',va='bottom',fontsize=20)
plt.xlabel("vehicles")
plt.ylabel("total delay of path")
figNumber = 1
figname='output/fig/fig{}.png'.format(figNumber)
while os.path.exists(figname):
    figNumber = figNumber+1
    figname='output/fig/fig{}.png'.format(figNumber)
if not os.path.exists(figname):
    plt.savefig(figname)
plt.show()