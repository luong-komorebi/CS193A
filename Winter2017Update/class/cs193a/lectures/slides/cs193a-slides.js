// Marty's little script to pre-process Reveal.js slide decks for use with CS 193A
(function() {
	var PRE_LONGCODE_THRESHOLD = 20;   // # of lines before adding 'longcode' class attribute

	var padLeft = function(s, len) {
		s = "" + s;
		while (s.length < len) {
			s = "&nbsp;" + s;
		}
		return s;
	};

	var trimEnd = function(s) {
		while (s[s.length - 1] == " " || s[s.length - 1] == "\n" || s[s.length - 1] == "\t" || s[s.length - 1] == "\r") {
			s = s.substring(0, s.length - 1);
		}
		return s;
	};

	// put line numbers at front of each line of pre code blocks
	var addLineNumbersInline = function() {
		var pres = document.querySelectorAll("pre code");
		for (var i = 0; i < pres.length; i++) {
			var pre = pres[i];
			if (pre.parentNode.classList.contains("nolinenumbers")) {
				continue;
			}
			var text = trimEnd(pre.innerHTML);
			var lines = text.split(/\r?\n/);
			var result = "";

			// don't add line numbers if just one line
			if (lines.length <= 1) {
				continue;
			} else if (lines.length >= PRE_LONGCODE_THRESHOLD) {
				pre.parentNode.classList.add("longcode");   // long chunks of code are more condensed
			}

			var numDigits = lines.length < 10 ? 1 : lines.length < 100 ? 2 : 3;
			for (var j = 0; j < lines.length; j++) {
				lines[j] = "<span class=\"linenumber\">" + padLeft("" + (j+1), numDigits) + "</span>&nbsp;&nbsp;" + lines[j];
			}
			pre.innerHTML = lines.join("\n");
		}
	};

	// put line numbers at front of each line of pre code blocks
	var addLineNumbersSeparate = function() {
		var pres = document.querySelectorAll("pre code");
		for (var i = 0; i < pres.length; i++) {
			var pre = pres[i];
			if (pre.parentNode.classList.contains("nolinenumbers")) {
				continue;
			}
			var text = trimEnd(pre.innerHTML);
			var lines = text.split(/\r?\n/);

			// don't add line numbers if just one line
			if (lines.length <= 1) {
				continue;
			} else if (lines.length >= PRE_LONGCODE_THRESHOLD) {
				pre.parentNode.classList.add("longcode");   // long chunks of code are more condensed
			}

			var result = "";
			var numDigits = /* lines.length < 10 ? 1 : */ lines.length < 100 ? 2 : 3;
			for (var j = 0; j < lines.length; j++) {
				lines[j] = padLeft("" + (j+1), numDigits) + "&nbsp;";
			}
			text = lines.join("\n");
			pre = pre.parentNode;   // grab the <pre>, not the <code>

			var parent = pre.parentNode;
			var sib = pre.nextSibling;
			parent.removeChild(pre);

			var newPre = document.createElement("pre");
			newPre.className = pre.className + " linenumbers";
			// newPre.style.cssFloat = "left";
			newPre.innerHTML = "<code style=\"float: left;\">" + text + "</code>";

			var newOuterDiv = document.createElement("div");
			newOuterDiv.className = "linenumbersouter";

			newOuterDiv.appendChild(newPre);
			newOuterDiv.appendChild(pre);

			parent.insertBefore(newOuterDiv, sib);
		}
	};

	// make "api" links into links to the Java API web site
	var javaApiLinks = function() {
		var JAVA_API_BASE_URL = "https://docs.oracle.com/javase/8/docs/api/$CLASSNAME.html";
		var ANDROID_API_BASE_URL = "https://developer.android.com/reference/$CLASSNAME.html";
		var STANFORD_ANDROID_LIBRARY_API_BASE_URL = "https://web.stanford.edu/class/cs193a/lib/javadoc/$CLASSNAME.html";
		var FIREBASE_API_BASE_URL = "https://firebase.google.com/docs/reference/android/$CLASSNAME";

		var codes = document.querySelectorAll(
				"code.api, code.javaapi, code.java-api"
				+ ", code.androidapi, code.android-api"
				+ ", code.stanfordandroidlibraryapi, code.stanfordandroidlib-api"
				+ ", code.firebaseapi, code.firebase-api"
		);
		for (var i = 0; i < codes.length; i++) {
			var className = codes[i].getAttribute("data-class");
			if (!className) {
				// allow the inner text itself to be the class name, such as <code class="javaapi">java.lang.String</code>
				if (codes[i].innerHTML.indexOf(".") >= 0) {
					className = codes[i].innerHTML.trim();
				}
			}
			if (!className) {
				continue;
			}
			className = className.replace(/\./g, "/");

			// insert a link to Oracle Java API or Google Android API
			var isStanford = className.indexOf("stanfordandroidlibrary") >= 0 || (codes[i].classList && codes[i].classList.contains("stanfordandroidlibraryapi"));
			var isAndroid = className.indexOf("android") >= 0 || (codes[i].classList && codes[i].classList.contains("androidapi"));
			var isFirebase = className.indexOf("firebase") >= 0 || (codes[i].classList && codes[i].classList.contains("firebaseapi"));
			var baseUrl = isStanford ? STANFORD_ANDROID_LIBRARY_API_BASE_URL :
					isAndroid ? ANDROID_API_BASE_URL :
					isFirebase ? FIREBASE_API_BASE_URL :
					JAVA_API_BASE_URL;

			var url = baseUrl.replace(/\$CLASSNAME/, className);
			var link = document.createElement("a");
			link.target = "_blank";
			link.href = url;
			codes[i].parentNode.insertBefore(link, codes[i]);
			codes[i].parentNode.removeChild(codes[i]);
			link.appendChild(codes[i]);
		}
	};

	// make external links with the "popup" class pop up in a new window
	var popupLinks = function() {
		var links = document.querySelectorAll("a.popup");
		for (var i = 0; i < links.length; i++) {
			var link = links[i];
			if (!link.target) {
				link.target = "_blank";
			}
		}
	};

	// remove initial \n from pre blocks (avoid extraneous blank line with highlight.js)
	// also replace tabs with 4 spaces
	var trimPreBlocks = function() {
		var pres = document.querySelectorAll("pre code");
		for (var i = 0; i < pres.length; i++) {
			var pre = pres[i];
			var text = pre.innerHTML;
			var changed = false;
			if (text[0] == "\r" || text[0] == "\n") {
				text = text.substring(1);
				changed = true;
			}
			if (text.indexOf && text.indexOf("\t") >= 0) {
				text = text.replace(/\t/g, "    ");
				changed = true;
			}

			if (changed) {
				pre.innerHTML = text;
			}
		}
	};

	// function to be called when the page loads
	var windowOnLoad = function() {
		javaApiLinks();
		popupLinks();
		trimPreBlocks();
		// addLineNumbersInline();
		addLineNumbersSeparate();
	};

	window.addEventListener("load", windowOnLoad, false);
})();

var REVEAL_FLAGS_STANDARD = {
	center: false,
	controls: true,
	history: true,
	keyboard: true,
	margin: 0.05,
	mouseWheel: true,
	progress: true,
	slideNumber: true,
};

var REVEAL_FLAGS_FULLSCREEN = {
	center: false,
	controls: false,
	history: true,
	keyboard: true,
	margin: 0.00,
	mouseWheel: true,
	progress: true,
	slideNumber: true,
	width: "100%",
	height: "100%"
};

var REVEAL_IS_FULLSCREEN = false;

function initializeSlides() {
	window.addEventListener("load", function() {
		Reveal.initialize(REVEAL_FLAGS_STANDARD);
		addFullScreenButton();
	});
	hljs.initHighlightingOnLoad();
}

function toggleFullScreen() {
	REVEAL_IS_FULLSCREEN = !REVEAL_IS_FULLSCREEN;
	Reveal.configure(REVEAL_IS_FULLSCREEN ? REVEAL_FLAGS_FULLSCREEN : REVEAL_FLAGS_STANDARD);
}

function addFullScreenButton() {
	var controls = document.querySelector("aside.controls");
	if (!controls) {
		return;
	}
	var butt = document.createElement("button");
	butt.id = "fullscreenbutton";
	butt.innerHTML = "&plusb;";   // &neArr;
	butt.title = "full-screen mode";
	butt.onclick = toggleFullScreen;
	controls.appendChild(butt);
}
