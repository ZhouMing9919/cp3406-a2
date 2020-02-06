package jcu.cp3406.numberbumper;

import android.os.NetworkOnMainThreadException;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

class TwitterWrapper {

    private ConfigurationBuilder configuration = new ConfigurationBuilder();
    private TwitterFactory factory;
    private Twitter twitter;

    TwitterWrapper(String consumerKey, String consumerSecret, String accessToken, String accessSecret) {
        configuration
                .setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessSecret);
        factory = new TwitterFactory(configuration.build());
        twitter = factory.getInstance();
    }

    void tweet(String message) throws TwitterException, NetworkOnMainThreadException {
        twitter.updateStatus(message);
    }
}
