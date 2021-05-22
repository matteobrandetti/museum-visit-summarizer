#!/usr/bin/python

import os
import json
import sys


class Visitor:
    def __init__(self, number, group_number):
        self.number = number
        self.group_number = group_number
        self.positions = list()
        self.presentations = list()

    def add_all_presentations(self, presentations:[]):
        self.presentations.extend(presentations)

    def add_all_positions(self, positions:[]):
        self.positions.extend(positions)


class Position:
    def __init__(self, name, start_time, end_time):
        self.name = name
        self.start_time = start_time
        self.end_time = end_time

    def __repr__(self):
        return "name:{0}, start_time:{1}, end_time:{2}".format(self.name, self.start_time, self.end_time)

    def __str__(self):
        return "name:{0}, start_time:{1}, end_time:{2}".format(self.name, self.start_time, self.end_time)


class Presentation:
    def __init__(self, name, start_time, end_time, has_stopped, rate):
        self.name = name
        self.start_time = start_time
        self.end_time = end_time
        self.has_stopped = has_stopped
        self.rate = rate

    def __repr__(self):
        return "name:{0}, start_time:{1}, end_time:{2}, rate:{3}".format(self.name, self.start_time, self.end_time,
                                                                         self.rate)

    def __str__(self):
        return "name:{0}, start_time:{1}, end_time:{2}, rate:{3}".format(self.name, self.start_time, self.end_time,
                                                                         self.rate)


def process_log_positions(path: str, filename: str, visitor: Visitor):
    positions = []
    with open(path + filename, "r") as file:
        for line in file:
            stripped_line = line.strip()
            if stripped_line != "presentations":
                tokens = stripped_line.split(",")
                if len(tokens) == 3:
                    start_time = tokens[0]
                    end_time = tokens[1]
                    pos_name = tokens[2]
                    pos = Position(pos_name, start_time, end_time)
                    positions.append(pos)
            else:
                break

    visitor.add_all_positions(positions)
    file.close()


def compute_position_name(path_to_new_file="Museum data/data.json", path_to_positions_file="Museum data/positions"
                                                                                           ".txt"):
    with open(path_to_new_file) as json_file:
        position_names = set()
        visitors = json.load(json_file)
        json_file.close()
        for v in visitors:
            for p in v["positions"]:
                position_names.add(p["name"])

    file_content = ""
    for p in position_names:
        print(p)
        file_content = file_content + p
        file_content = file_content + ","

    pos_names_file = open(path_to_positions_file, "w")
    pos_names_file.write(file_content)
    pos_names_file.close()


def process_log_presentations(path: str, file: str, visitor: Visitor):
    presentations = []
    with open(path + file, "r") as file:
        start_printing = False
        for line in file:
            stripped_line = line.strip()
            if stripped_line == "presentations":
                start_printing = True
            else:
                if stripped_line == "events":
                    break

            if start_printing:
                tokens = stripped_line.split(",")
                if len(tokens) >= 6:
                    start_time = tokens[0]
                    end_time = tokens[1]
                    pos = tokens[2]
                    stopped = tokens[4]
                    rate = tokens[5]
                    if stopped == "User":
                        has_stopped = True
                    else:
                        has_stopped = False

                    presentation = Presentation(pos, start_time, end_time, has_stopped, rate)
                    presentations.append(presentation)

    visitor.add_all_presentations(presentations)
    file.close()


def extract_visitor(filename: str):
    _, visitor_number, group_number = filename.split("_")
    v = Visitor(visitor_number, group_number)
    return v


def create_json(path_to_new_filename="Museum data/", json_filename="data.json", path_to_log = 'Museum data/Logs/'):
    visitors = []
    for subdir, dirs, files in os.walk(path_to_log):
        for filename in files:
            visitor = extract_visitor(filename[0:-4])
            visitors.append(visitor)
            process_log_positions(path_to_log, filename, visitor)
            process_log_presentations(path_to_log, filename, visitor)

    visitors = filter(lambda v: len(v.positions) != 0, visitors)

    with open(path_to_new_filename + json_filename, 'w') as outfile:
        json_data = json.dumps([v.__dict__ for v in visitors], default=lambda o: o.__dict__, indent=4)
        outfile.write(json_data)
        outfile.close()


if len(sys.argv) == 4:
    # arg1: path where to locate the new json file
    # arg2: new json file name, e.g. data.json
    # arg3: path to the log directory, where the csv files are located
    print('Number of arguments:', len(sys.argv), 'arguments.')
    print('Argument List:', str(sys.argv))
    path_to_new_filename = str(sys.argv[1])
    json_filename = str(sys.argv[2])
    path_to_logs_dir = str(sys.argv[3])
    compute_position_name()
    create_json(path_to_new_filename, json_filename, path_to_logs_dir)

else:
    print("Wrong number of arguments, falling back on default values")
    compute_position_name()
    create_json()
