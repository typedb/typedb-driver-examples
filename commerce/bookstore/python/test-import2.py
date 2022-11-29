import csv

items = []
with open("data/books.csv", encoding='latin-1') as data:  # 1
    for row in csv.DictReader(data, skipinitialspace=True):
        #print(row)
        item = {key: value for key, value in row.items()}  # fieldnames (keys) are taken from the first row
        items.append(item)  # 2
print(items)