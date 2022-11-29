import pandas as pd

# Loading data

books = pd.read_csv("data/books.csv", sep=";", on_bad_lines='skip', encoding="latin-1")
books.columns = ['ISBN', 'bookTitle', 'bookAuthor', 'yearOfPublication', 'publisher',
                 'imageUrlS', 'imageUrlM', 'imageUrlL']
'''
users = pd.read_csv('data/users.csv', sep=';',
                    on_bad_lines='skip', encoding="latin-1")
users.columns = ['userID', 'Location', 'Age']
ratings = pd.read_csv('data/ratings.csv', sep=';',
                      on_bad_lines='skip', encoding="latin-1")
ratings.columns = ['userID', 'ISBN', 'bookRating']
'''

books.drop(['imageUrlS', 'imageUrlM', 'imageUrlL', 'yearOfPublication'], axis=1, inplace=True)

#books = books.dropna()

#print(books.shape)
#print(users.shape)
#print(ratings.shape)

print(books.head())

'''
print(books.dtypes)
'''

'''
for x in books.bookTitle:
    print(x)
'''

#for index, row in books.iterrows():
#    print(row['ISBN'], row['bookTitle'], row['bookAuthor'], row['publisher'])




