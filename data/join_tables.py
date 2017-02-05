#!/usr/bin/env python

import csv

with open('user-role.csv', 'rb') as file_a, open('user-permission.csv', 'rb') as file_b:
    data_a = csv.reader(file_a)
    data_b = dict(csv.reader(file_b))  # <-- dict
    with open('out.csv', 'wb') as file_out:
        csv_out = csv.writer(file_out)
        for word, num_a in data_a:
            csv_out.writerow([word, num_a, data_b.get(word, '')])  # <-- edit
