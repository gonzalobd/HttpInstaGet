# HttpInstaGet

This project fetch live data about likes and comments from an Instagram account to Kafka Queues. Class NewMediaGuetter is asking Instagram API about the last 20 pictures. This 20 pictures go to a queue and classes CommentInfo and LikeInfo ask for all likes/comments of those 20 pictures and send them to Kafka topics "like" and "comment"

All comments and likes sent are stored in a Serialized Java Object (Kryo serialization) to don't send them again.

Sorry for the comments in spanish inside the code!
