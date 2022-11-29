import csv

print('Books parsing started')
books = []
# n = 1
with open("data/books_lite.csv", encoding='latin-1') as data:  # 1
    for row in csv.DictReader(data, delimiter=";", skipinitialspace=True):
        item = {key: value for key, value in row.items()}  # fieldnames (keys) are taken from the first row
        books.append(item)  # 2
        # print(n, item)
        # n += 1
print('Books parsing ended')

print('Review parsing started')
reviews = []
n = 1
y = 1
ratings_file = open("data/ratings_lite.csv", "w", newline='', encoding='latin-1')
fieldnames = ['User-ID', 'ISBN', 'Book-Rating']
writer = csv.DictWriter(ratings_file, fieldnames=fieldnames)
writer.writeheader()
with open("data/ratings.csv", encoding='latin-1') as data:  # 1
    for row in csv.DictReader(data, delimiter=";", skipinitialspace=True):
        item = {key: value for key, value in row.items()}  # fieldnames (keys) are taken from the first row
        for b in books:
            if item['ISBN'] == b['ISBN']:
                print(n, item, y, b)
                n += 1
                writer.writerow(item)
                reviews.append(item)  # 2
        y += 1
ratings_file.close()
print('Review parsing ended')

print('Users parsing started')
n = 1
y = 1
users_file = open("data/users_lite.csv", "w", newline='', encoding='latin-1')
fieldnames = ['User-ID', 'Location', 'Age']
writer = csv.DictWriter(users_file, fieldnames=fieldnames)
writer.writeheader()
with open("data/users.csv", encoding='latin-1') as data:  # 1
    for row in csv.DictReader(data, delimiter=";", skipinitialspace=True):
        item = {key: value for key, value in row.items()}  # fieldnames (keys) are taken from the first row
        for r in reviews:
            if item['User-ID'] == r['User-ID']:
                print(n, item, y, r)
                n += 1
                writer.writerow(item)  # bug - creates multiple same users, by the number of reviews from that user
        y += 1
users_file.close()
print('Users parsing ended')

# Additional dataset preparations are in https://github.com/izmalk/prepare_dataset
