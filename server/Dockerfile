# syntax=docker/dockerfile:1

FROM python:3.8-slim-buster

RUN mkdir -p /server
RUN chown root /server
USER root
WORKDIR /server

COPY requirements.txt requirements.txt
RUN pip3 install -r requirements.txt

COPY . .

CMD [ "python3", "-m" , "flask", "run", "--host=0.0.0.0"]