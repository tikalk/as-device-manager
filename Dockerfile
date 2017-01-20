###
# vert.x docker device-manager using a Java verticle packaged as a fatjar
# To build:
#  docker build -t fleettracker/ft-device-manager .
# To run:
#   docker run -t -i -p 8080:8080 fleettracker/ft-device-manager
###

FROM java:8

EXPOSE 4080
# Copy your fat jar to the container
ADD build/distributions/ft-device-manager-3.1.0.tar.gz /ft-device-manager

# Launch the verticle
ENV WORKDIR /ft-device-manager
ENTRYPOINT ["sh", "-c"]
CMD ["cd $WORKDIR ; ./device-manager.sh"]
