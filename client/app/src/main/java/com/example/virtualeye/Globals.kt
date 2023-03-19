package com.example.virtualeye

/**
 * This object contains global variables used throughout the application for navigation.
*/

object Globals {

    // A mutable list of strings that represents the path to be followed during navigation.
    var path = mutableListOf<String>()
    // A mutable list of strings that represents the directions to be given during navigation.
    var directions = mutableListOf<String>()
    // A mutable list of strings that represents the bearings to be followed during navigation.
    var bearings = mutableListOf<String>()
    // A nullable string that represents the initial bearing for the navigation path.
    var initBearing: String? = null
    // A nullable string that represents the starting location for the navigation path.
    var startLocation: String? = null
    // A nullable string that represents the destination location for the navigation path.
    var destLocation: String? = null
    
}