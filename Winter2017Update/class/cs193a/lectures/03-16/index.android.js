/**
 * CS 193A, Marty Stepp
 * This React Native app is a remake of the very first program we wrote in this course,
 * the "Berkeley Number Guessing Game" that asks the user to choose the larger of two numbers.
 * This is a demonstration of the React Native framework for developing cross-platform mobile apps.
 *
 * https://github.com/facebook/react-native
 * @flow
 */

// import various required libraries
import React, { Component } from 'react';
import { Alert, AppRegistry, Button, StyleSheet, Text, View } from 'react-native';

// constant for max value to use for number generation
var MAX = 10;

/* This class represents my app as a React 'component.' */
export default class BerkeleySucks extends Component {
    /* Constructor is called when app is loading up. Similar to onCreate. */
    constructor(props) {
        super(props);
        
        // 'state' is variables that are used by the app and its UI;
        // if state variables update, the UI will automatically refresh
        this.state = {
            num1: 0,
            num2: 0,
            points: 0,
        };
        this.roll();
    }

    /* This function chooses two new random values to put on the buttons. */
    roll() {
        // choose two unique random integers
        var a = parseInt(Math.random() * MAX);
        var b = a;
        while (a == b) {
            b = parseInt(Math.random() * MAX);
        }
        this.state.num1 = a;
        this.state.num2 = b;
    }

    /*
     * This function is called when the left button is pressed.
     * It checks whether the left button's number is larger and adjusts the player's score.
     */
    onLeftPress() {
        // Alert.alert("Left!");
        if (this.state.num1 > this.state.num2) {
            this.state.points++;
        } else {
            this.state.points--;
        }
        this.roll();
        this.updateState();
    }

    /*
     * This function is called when the right button is pressed.
     * It checks whether the right button's number is larger and adjusts the player's score.
     */
    onRightPress() {
        // Alert.alert("Right!");
        if (this.state.num2 > this.state.num1) {
            this.state.points++;
        } else {
            this.state.points--;
        }
        this.roll();
        this.updateState();
    }

    /* Common helper function to update the game's state. */
    updateState() {
        this.setState({
            num1: this.state.num1,
            num2: this.state.num2,
            points: this.state.points
        });
    }

    /*
     * This required function describes the UI and layout of the app.
     * It returns a description of the app's screen layout using XML
     * embedded within this JavaScript file, which is called JSX.
     */
    render() {
        return (
            <View style={styles.container}>
                <Text style={styles.welcome}>
                    Number Guessing Game!
                </Text>

                <Text style={styles.instructions}>
                    Click the button that displays the larger number.
                </Text>

                <View style={styles.horizontalcontainer}>
                    <Button style={styles.buttonstyle} id="leftbutton" title={"" + this.state.num1}
                        onPress={this.onLeftPress.bind(this)}
                    />
                    <Button style={styles.buttonstyle} id="rightbutton" title={"" + this.state.num2}
                        onPress={this.onRightPress.bind(this)}
                    />
                </View>

                <Text style={styles.instructions}>
                    Score: {"" + this.state.points}
                </Text>
            </View>
        );
    }
}

/*
 * This area describes styles and appearance in the app.
 * Many of these properties come from web programming's
 * Cascading Style Sheets (CSS).
 */
const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
    },
    horizontalcontainer: {
        flex: 1,
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
        width: '100%'
    },
    welcome: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
    },
    buttonstyle: {
        flex: 1,
        fontSize: 40,
        margin: 10,
        padding: 40,
    },
    instructions: {
        textAlign: 'center',
        color: '#333333',
        marginBottom: 5,
    },
});

// registers the app so it will be loaded
AppRegistry.registerComponent('BerkeleySucks', () => BerkeleySucks);
