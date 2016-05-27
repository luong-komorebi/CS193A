// Marty Stepp's Stanford CS course web site script
// Sets up various things on my course web site pages,
// such as zebra striping for some table rows,
// links to files, email addresses and campus buildings.

// wrap entire script in an anonymous function to avoid polluting global scope
// (JS module pattern)
(function() {
	"use strict";

	var DEPARTMENT_EMAIL_DOMAIN = "http://web.stanford.edu/class/cs193a/cs.stanford.edu";
	var UNIVERSITY_EMAIL_DOMAIN = "http://web.stanford.edu/class/cs193a/stanford.edu";
	var DEFAULT_EMAIL_DOMAIN = DEPARTMENT_EMAIL_DOMAIN;
	var WEEK1_SUN;
	var DATES = {};
	var DAYS_OF_WEEK = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
	var MONTHS_OF_YEAR = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
	setupReferenceDates();

	// max number of files that will be shown before initially collapsing
	var MAX_FILES_LENGTH = 99;

	// URL stub for campus map building links
	// (this list is woefully incomplete; if you need to add a building, go ahead)
	var BUILDING_IDS = {
		// "braun": "01-320",
		"annenberg": "03-010",
		"annenberg auditorium": "03-010",
		"art4": "03-010",
		"bishop": "08-350",
		"bishop auditorium": "08-350",
		"braun": "07-210",
		"braun auditorium": "07-210",
		"braun corner": "01-320",
		"cemex": "08-050C",
		"cemex auditorium": "08-050C",
		"cubberley": "03-300",
		"cubberley auditorium": "03-300",
		"dinkelspiel auditorium": "02-200",
		"dinkelspiel": "02-200",
		"educ": "03-300",
		"education": "03-300",
		"encina": "06-010",
		"encina hall center": "06-010",
		"encinaw": "06-020",
		"encina hall west": "06-020",
		"encina west": "06-020",
		"gates": "07-450",
		"geology": "01-320",
		"gilbert": "07-420",
		"herrin hall": "07-410",
		"herrin": "07-410",
		"hewlett": "04-510",
		"history": "01-200",
		"jacks": "01-460",
		"jordan hall (psychology)": "01-420",
		"jordan hall": "01-420",
		"jordan": "01-420",
		"lane": "01-200",
		"lane history corner": "01-200",
		"lathrop": "08-350",
		"lathrop library": "08-350",
		"margaret jacks hall": "01-460",
		"margaret jacks": "01-460",
		"memorial auditorium": "08-300",
		"memorial": "08-300",
		"mudd": "07-210",
		"mudd chemistry": "07-210",
		"sloan mathematics": "01-380",
		"sloan": "01-380",
		"thornton": "04-720",
		"tressider": "02-300",
		"wallenberg": "01-160",
		"blume earthquake center (civil eng)": "02-540",
		"blume earthquake center": "02-540",
		"blume": "02-540",
		"civil eng": "02-540",
		"mcclatchy": "01-120",
		"mcclatchy hall": "01-120",
		"building 300": "01-300",
		"building 300, main quad": "01-300",
		// "160": "01-160"
		"50": "01-050",
		"540": "02-540",
	};
	var MAPS_URL = "http://campus-map.stanford.edu/index.cfm?ID=";

	// Turns spans with class of "building" into links to a campus map to that building.
	function buildingMapLinks() {
		var cells = document.querySelectorAll(".building");
		for (var i = 0; i < cells.length; i++) {
			var addr = cells[i].innerHTML;
			if (addr == "TBD" || addr == "TBA") {
				continue;
			}
			
			var tokens = addr.split(/[ \-]+/);
			var building = tokens[0];
			var roomNumber = "";

			if (tokens.length >= 2) {
				roomNumber = " " + tokens[1];
			}

			var buildingLC = building.toLowerCase();
			var buildingID = addr;
			if (BUILDING_IDS[buildingLC]) {
				buildingID = BUILDING_IDS[buildingLC];
			} else {
				// possible weird building link like "160-321";
				// check for just the first part ("160") in BUILDING_IDS map; if it's there, use that;
				// if not, assume most common "01-" prefix, e.g. "01-160"
				if (BUILDING_IDS[building]) {
					buildingID = BUILDING_IDS[building];
				} else {
					buildingID = "01-" + building;
				}
			}
			cells[i].update("<a target=\"_blank\" href=\"" + MAPS_URL + buildingID + "\">" + cells[i].innerHTML + "</a>");
		}
	}

	// Displays a warning message on the page if this is not the current quarter's web site.
	function checkPageOutOfDate() {
		// TODO: update this code for Stanford
		if (location.href.match(/cs106.\.stanford\.edu/)) {
			return;
		}

		// try to figure out what quarter this web site is for
		var quarter = location.href.replace(/.*\/cse([0-9a-zA-Z]+)\/([0-9a-zA-Z]+)\/.*/, "$2");
		if (!quarter || quarter.toLowerCase() == "currentqtr" || quarter.length != 4) {
			return;
		}
		var quarterYear = 2000 + parseInt(quarter.substring(0, 2), 10);   // "11wi" -> 11
		var quarterQtr  = quarter.substring(2);                // "11wi" -> "wi"

		var now = new Date();
		var currentYear = now.getYear();
		if (currentYear < 1000) {
			// non-IE<=8 returns year since 1900; IE<=8 returns actual year
			currentYear += 1900;
		}
		var currentQtr  = "";       // e.g. "11au"
		var currentQtrQtr = "";     // e.g. "au"
		var obsolete = false;

		// try to figure out what quarter it is currently (roughly)
		var QUARTERS = ["wi", "sp", "su", "au"];
		var QUARTER_START = {
			"wi": "Jan 1",
			"sp": "Mar 20",
			"su": "Jun 24",
			"au": "Sep 20"
		};
		var QUARTER_END = {
			"wi": "Mar 19",
			"sp": "Jun 23",
			"su": "Sep 19",
			"au": "Dec 31"
		};
		var QUARTER_FULL_NAME = {
			"wi": "Winter",
			"sp": "Spring",
			"su": "Summer",
			"au": "Autumn"
		};

		// "Dec 6 2011 8:15 AM"
		for (var i = 0; i < QUARTERS.length; i++) {
			var qtrName = QUARTERS[i];
			var dateStart = new Date(Date.parse(QUARTER_START[qtrName] + " " + currentYear + " 12:00 AM"));
			var dateEnd   = new Date(Date.parse(QUARTER_END[qtrName]   + " " + currentYear + " 11:59 PM"));
			if (dateStart <= now && now <= dateEnd) {
				// found the current quarter!  now see if this web page is from that quarter.
				currentQtr = currentYear % 100 + qtrName;
				currentQtrQtr = qtrName;
				break;
			}
		}

		if (quarterYear < currentYear) {
			obsolete = true;                    // this web site comes from a past year
		} else if (quarter && currentQtr) {
			var quarterIndex = QUARTERS.indexOf(quarterQtr);
			var currentQtrIndex = QUARTERS.indexOf(currentQtrQtr);
			obsolete = quarter != currentQtr &&
				(quarterIndex >= 0 && currentQtrIndex >= 0 && quarterIndex < currentQtrIndex);   // this web site may come from a past quarter in the same year
		}

		var quarterFullName = QUARTER_FULL_NAME[quarterQtr] + " " + quarterYear;
		var currentQuarterFullName = QUARTER_FULL_NAME[currentQtrQtr] + " " + currentYear;

		if (obsolete) {
			var div = document.createElement("div");

			// try to figure out what course this web site is for
			var course = location.href.replace(/.*\/cse([0-9a-zA-Z]+)\/.*/, "$1") || (location.href.match(/143/) ? "143" : "142");
			var websiteLink = "http://cs" + course + ".stanford.edu/";

			div.className = "excitingnews";
			div.update("NOTE: This old web site is <strong>out of date</strong>.  " +
					"This is the course web site from a past quarter, <strong>" + quarter + "</strong> (" + quarterFullName + "), " +
					"but the current quarter is <strong>" + currentQtr + "</strong> (" + currentQuarterFullName + ").  " +
					"If you are a current student taking the course, this is not your class web site, " +
					"and you should visit the <a href=\"" + websiteLink + "\">current class web site</a> instead.");

			var container = $$("div.centerpane")[0] || $("container") || document.body;
			container.insertBefore(div, container.firstChild);
		}
	}

	// Inserts information about course staff as table rows.
	// sectionLeaderList = [
	//	{name: "Alex Hsu",          email: "kzm",       sectiontime: "Wed 7:00 PM",   sectionroom: "420-371"}, ... ]
	function insertStaffTableRows() {
		if (typeof(sectionLeaderList) == "undefined") {
			return;
		}

		var templateRow = document.querySelector("#sltemplaterow, .sltemplaterow");
		if (!templateRow) {
			return;
		}

		var slTable = $("sltable");
		if (!slTable) {
			return;
		}
		var slTableBody = slTable.querySelector("tbody");

		for (var i = 0; i < sectionLeaderList.length; i++) {
			var slRow = templateRow.cloneNode(true);
			slRow.style.display = "";
			slTableBody.appendChild(slRow);

			var sl = sectionLeaderList[i];
			for (var prop in sl) {
				if (sl.hasOwnProperty(prop)) {
					var slRowElement = slRow.querySelector(".sl" + prop);
					if (slRowElement) {
						slRowElement.innerHTML = sl[prop];
					}
				}
			}
			var slImage = slRow.querySelector(".slimage");
			if (slImage) {
				var slNameLC = sl["name"].toLowerCase().replace(/ /g, "-");
				slImage.src = slImage.src.replace(/SLNAME/, slNameLC);
			}
		}
	}

	// Returns true if the given image is indicating that its area is expanded.
	function isExpanded(img) {
		return img.src.match(/minus/);
	}

	// Sets the given image to be a plus or minus icon.
	function swapPlusMinus(img) {
		if (isExpanded(img)) {
			img.src = img.src.replace(/minus/, "plus");
		} else {
			img.src = img.src.replace(/plus/, "minus");
		}
	}

	// Toggles an element being expanded or collapsed.
	function toggleCollapsed(collapse) {
		collapse.select("ul").each(Element.toggle);
		var img = collapse.select("img.plusicon")[0];
		swapPlusMinus(img);
	}

	// Handles click on a link around a plusicon image.
	function collapseableClick(event, element) {
		if (event) {
			event.stop();
		}
		if (!element) {
			element = this;
		}
		var collapse = element.up(".collapseable");
		if (!collapse) { return; }

		toggleCollapsed(collapse);
	}

	// Turns spans with class of "ema" into links to email that person.
	function emailAddressLinks() {
		var cells = $$(".ema");
		for (var i = 0; i < cells.length; i++) {
			var addr = cells[i].textContent ? cells[i].textContent : cells[i].innerText;
			var linkText = addr;
			var domain = DEFAULT_EMAIL_DOMAIN;
			if (cells[i].hasClassName("deptema")) {
				domain = DEPARTMENT_EMAIL_DOMAIN;
			} else if (cells[i].hasClassName("universityema")) {
				domain = UNIVERSITY_EMAIL_DOMAIN;
			}

			if (!cells[i].hasClassName("hideema")) {
				linkText += "@" + domain;
			}
			cells[i].update("<a href=\"mailto:" + addr + "@" + domain + "\"><img src=\"images/icon_email.gif\"/*tpa=http://web.stanford.edu/class/cs193a/images/icon_email.gif*/ class=\"icon\" alt=\"icon\" width=\"16\" height=\"16\" />&nbsp;" + linkText + "</a>");
		}
	}

	// Expands all lists of files on lecture calendar page.
	function expandAllClick(event) {
		if (event) {
			event.stop();
		}

		var img = this.select("img.plusicon")[0];
		if (!img) { return; }

		var expand = !!img.src.match(/plus/);
		swapPlusMinus(img);

		$$(".collapseable").each(function(collapse) {
			// .select("ul").each(Element.toggle);
			var img = collapse.select("img.plusicon")[0];
			if (!!isExpanded(img) !== expand) {
				toggleCollapsed(collapse);
			}
		});
	}

	/** Returns all query parameters on the page as a [key => value] hash. */
	function getQueryParams() {
		var hash = {};
		if (location.search && location.search.length >= 1) {
			var url = location.search.substring(1);
			var chunks = url.split(/&/);
			for (var i = 0; i < chunks.length; i++) {
				var keyValue = chunks[i].split("=");
				if (keyValue[0]) {
					var thisValue = true;
					if (typeof(keyValue[1]) !== "undefined") {
						thisValue = encodeURIComponent(keyValue[1]);
						thisValue = thisValue.replace(/[+]/g, " ");  // unescape URL spaces
					}
					hash[keyValue[0]] = thisValue;
				}
			}
		}
		return hash;
	}

	// try to highlight current date table cell on lecture calendar page
	function highlightCurrentDate() {
		$$(".folder.subheading").each(function(element) {
			var text = typeof(element.textContent) !== "undefined" ? element.textContent : element.innerText;
			text = text.trim().replace(/\n.*/gi, "");
			if (text.match(/[0-9]{1,2}-[0-9]{1,2}/)) {
				// then this is a date
				var today = new Date();
				var tokens = text.split(/-/);
				var month = parseInt(tokens[0], 10);
				var day = parseInt(tokens[1], 10);
				if (month == today.getMonth() + 1 && day == today.getDate()) {
					var td = element.up("td");
					if (td) {
						td.addClassName("today");
						td.id = "today";
					} else {
						element.addClassName("today");
						element.id = "today";
					}

					if (location.hash == "#today") {
						// this line might seem useless, but re-setting the #hash makes
						// the browser jump down to the element with that newly-set id
						location.hash = "#today";
					}
				} else if (month > today.getMonth() + 1 ||
						(month == today.getMonth() + 1 && day > today.getDate())) {
					// let's also set a style on days in the future, just for fun
					var td = element.up("td");
					if (td) {
						td.addClassName("future");
					} else {
						element.addClassName("future");
					}
				}
			}
		});
	}

	// Turns links with class of "honorcode" into links that pop up a reminder to obey the course honor code
	function honorCodeLinks() {
		var FADESPEED = 0.5;
		if ($("honorcodeaccept")) {
			$("honorcodeaccept").onclick = function() {
				if ($("honorcodeaccept").popup) {
					// Event.simulate($("honorcodeaccept").link, "click");
					location.href = $("honorcodeaccept").href;
				} else {
					location.href = $("honorcodeaccept").href;
				}
				$("honorcodemessage").fade({duration: FADESPEED});
			};
		}
		if ($("honorcodecancel")) {
			$("honorcodecancel").observe("click", function() {
				$("honorcodemessage").fade({duration: FADESPEED});
			});
		}

		$$("a.honorcodelink").each(function(link) {
			// turn off popup in new tab because of interstitial popup window
			if (link.hasClassName("popup")) {
				link.target = "";
			}

			link.observe("click", function(event) {
				event = event || window.event;
				event.stop();
				$("honorcodeaccept").link = link;
				$("honorcodeaccept").href = link.href;
				$("honorcodeaccept").popup = link.hasClassName("popup");
				$("honorcodemessage").appear({duration: FADESPEED});
				return false;
			});
		});
	}

	function insertDates() {
		// inject each date's month/day into "date" spans sequentially
		//                    Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec
		var daysInMonth = [-1, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
		var month = 0;
		var day = 0;
		var year = 2012;   // hard-code 2012 because course web site will not change year in the future
		if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
			daysInMonth[2]++;  // leap years
		}

		var today = new Date();
		var tomorrow = new Date();
		tomorrow.setDate(today.getDate() + 1);
		var yesterday = new Date();
		yesterday.setDate(today.getDate() - 1);

		$$(".date").each(function(element) {
			if (element.innerHTML) {
				var tokens = element.innerHTML.split(/\//);
				month = parseInt(tokens[0], 10);
				day = parseInt(tokens[1], 10);
			} else if (month > 0 && day > 0) {
				day++;
				if (day > daysInMonth[month]) {
					day = 1;
					month = (month % 12) + 1;
				}
				element.update(month + "/" + day);
			}

			var td = element.up("td");
			if (td) {
				var thisDate = new Date(year, month - 1, day);
				if (thisDate > yesterday && thisDate <= today) {
					td.addClassName("today");
				}
			}
		});
	}

	// Sets +/- icons to expand/collapse various groups of files on the Lectures page.
	function plusMinusLinks() {
		$$(".plusicon").each(function(el) {
			var a = el.up("a");
			a.href = "#";

			if (!a || a.id == "expandall") { return; }
			a.observe("click", collapseableClick);

			var collapse = el.up(".collapseable");
			if (collapse) {
				collapseableClick(null, el);   // initially collapsed
			}
		});

		if ($("expandall")) {
			$("expandall").observe("click", expandAllClick);
		}
	}

	// Shows any "delayed" sections such as labs or sections to pop up on certain dates.
	// Useful for setting up an initially hidden div that will suddenly show up later.
	// Example:
	// <div class="assignmentarea delayed" style="display: none">
	//     <span class="showdate" style="display: none">Dec 6 2011 8:15 AM</span>
	//     ...
	// </div>
	function processDelayedContent() {
		var queryParams = getQueryParams();
		var changed = false;
		$$(".delayed").each(function(element) {
			var showDateText = "";
			replaceRelativeDate(element, /* skipInnerHTML */ true);

			// one option: another class attribute with the date/time to show it
			var classes = element.className.split(/[ ]+/);
			for (var i = 0; i < classes.length; i++) {
				if (classes[i].match(/delayed_/) || classes[i].match(/showdate_/)) {
					// "delayed_May_24_2011_11__00_PM"
					showDateText = classes[i].replace(/delayed_/, "").replace(/showdate_/, "");
					showDateText = showDateText.replace(/__/g, ":");
					showDateText = showDateText.replace(/_/g, " ");
					break;
				}
			}

			// other option: a span with the date/time to show it
			if (!showDateText) {
				var showDateSpan = element.select(".showdate")[0];
				if (showDateSpan) {
					showDateText = showDateSpan.innerHTML;
					if (!queryParams["ta"] || queryParams["clean"]) {
						showDateSpan.parentNode.removeChild(showDateSpan);
					}
				}
			}

			// can also have a "hide date" where the element will go away
			var hideDateText = "";
			var hideDateSpan = element.select(".hidedate")[0];
			if (hideDateSpan) {
				hideDateText = hideDateSpan.innerHTML;
				hideDateSpan.parentNode.removeChild(hideDateSpan);
			} else {
				for (var i = 0; i < classes.length; i++) {
					if (classes[i].match(/hidedate_/)) {
						// "delayed_May_24_2011_11__00_PM"
						hideDateText = classes[i].replace(/hidedate_/, "");
						hideDateText = hideDateText.replace(/__/g, ":");
						hideDateText = hideDateText.replace(/_/g, " ");
						break;
					}
				}
			}

			var now = new Date();
			if (showDateText) {
				var showDate = new Date(Date.parse(showDateText));
				if (showDate) {
					if (queryParams["ta"] || now >= showDate) {
						if (!$(element).visible()) {
							changed = true;
						}
						$(element).show();
						$(element).style.visibility = "";
						if (queryParams["ta"] || queryParams["debug"]) {
							console.log("showing (because after " + showDate + "): " + element + " id=" + element.id + " class=" + element.className + ", display=" + element.style.display);
						}
					} else {
						if (queryParams["ta"] || queryParams["debug"]) {
							console.log("NOT showing (because not after " + showDate + "): " + element + " id=" + element.id + " class=" + element.className + ", display=" + element.style.display);
						}
					}
				}

				if (showDateSpan && queryParams["ta"] && now < showDate) {
					showDateSpan.addClassName("showdatevisible");
					showDateSpan.title = "This content on the page will become visible to the students at the given date/time: " + showDate;
					if (!$(element).visible()) {
						changed = true;
					}
					$(showDateSpan).show();
					$(element).style.visibility = "";
				}
			}

			if (hideDateText) {
				var hideDate = new Date(Date.parse(hideDateText));
				if (hideDate && now >= hideDate) {
					if ($(element).visible()) {
						changed = true;
					}
					$(element).remove();
				}
			}
		});

		if (changed) {
			// run again after browser has laid out other previously hidden content,
			// which is needed if there are any nested "delayed" elements (for some reason)
			setTimeout(processDelayedContent, 50);
		}
	}

	// Makes "section cells" link to building info about that section.
	function sectionCellLinks() {
		$$(".sectioncell").each(function(el) {
			// "http://www.cs.stanford.edu/education/courses/cse154/10su/staff.shtml" -> "154"
			var course = location.href.replace(/.*\/cse([0-9]{3,4})[a-z]?\/.*/, "$1");
			var quarter = "AUT2011";

			// "http://www.cs.stanford.edu/education/courses/cse154/10su/staff.shtml" -> "10 su"
			var qtrStr = location.href.replace(/.*\/([0-9]{2})([a-zA-Z]{2})\/.*/, "$1 $2");

			var today = new Date();
			var year = today.getYear();
			if (year < 1000) {
				// non-IE<=8 returns year since 1900; IE<=8 returns actual year
				year += 1900;
			}

			var qtrMap = {
				"wi" : "WIN",
				"sp" : "SPR",
				"su" : "SUM",
				"au" : "AUT"
			};

			if (qtrStr.length > 0) {
				var tokens = qtrStr.split(/ /);
				if (tokens.length >= 2) {
					year = parseInt(tokens[0], 10) + 2000;
					var qtr = qtrMap[tokens[1]];
					quarter = qtr + year;
				}
			}

			el.update("<a target=\"_blank\" href=\"http://www.stanford.edu/students/timeschd/" + quarter + "/cse.html#cse" + course + "\">" + el.innerHTML + "</a>");
		});
	}

	function getTextContent(element) {
		try {
			if (element.value !== undefined) {
				return element.value;
			} else if (element.textContent !== undefined) {
				return element.textContent;
			} else if (element.innerText !== undefined) {
				return element.innerText;
			} else if (element.firstChild !== undefined && element.firstChild.nodeValue !== undefined) {
				return element.firstChild.nodeValue;
			}
		} catch (e) {}

		return null;
	}

	function setTextContent(element, text) {
		try {
			if (element.textContent !== undefined) {
				element.textContent = text;
			} else if (element.value !== undefined) {
				element.value = text;
			} else if (element.innerText !== undefined) {
				element.innerText = text;
			} else if (element.firstChild !== undefined) {
				element.firstChild.nodeValue = text;
			}
		} catch (e) {}

		return element;
	}

	// <span class="showdate" style="display: none">Week1 Fri 8:00 AM</span>
	// Due <span class="insertdate" rel="Week2 Wed"></span>, 11:30pm. <br />
	function setupReferenceDates() {
		if (typeof(WEEK1_SUN) == "undefined" || WEEK1_SUN === null) {
			if ($("WEEK1_SUN")) {
				WEEK1_SUN = new Date(Date.parse($("WEEK1_SUN").getAttribute("content") || $("WEEK1_SUN").getAttribute("rel") || $("WEEK1_SUN").innerHTML));
			} else {
				var meta = $$("meta[name='WEEK1_SUN']")[0];
				WEEK1_SUN = meta.getAttribute("content");
			}
		}
		if (!WEEK1_SUN) {
			console.log("No WEEK1_SUN variable found.  Unable to compute relative dates.");
			return;
		}

		// set up reference dates of the week for each week of the quarter
		var date = new Date(WEEK1_SUN);
		for (var i = 1; i <= 20; i++) {
			for (var day = 0; day < DAYS_OF_WEEK.length; day++) {
				var date2 = new Date(date);
				DATES["Week" + i + " " + DAYS_OF_WEEK[day].substring(0, 3)] = date2;
				date.setDate(date.getDate() + 1);   // handles month wrapping
			}
		}
	}

	// Replaces any element inner text that contains a relative date such as "Week2 Fri"
	// with the actual absolute date such as "Fri Mar 17".
	function replaceRelativeDate(element, skipInnerHTML) {
		// first look for delayed_DATE class class
		// <div class="delayed delayed_Week1_Tue_8__00_AM" style="display: none;">
		// becomes
		// <div class="delayed delayed_Apr_17_2013_8__00_AM" style="display: none;">
		var classNames = element.classList || element.className.split(/[ ]+/);
		for (var i = 0; i < classNames.length; i++) {
			var classStr = classNames[i];   // "delayed_Week1_Tue_8__00_AM"

			// match = ["delayed_Week1_Tue_8__00_AM", "1", "Tue"]
			var datePattern = /delayed_week[_]*(\d+)[_]*(sun|mon|tue|wed|thu|fri|sat)/i;
			var match = classStr.match(datePattern);
			if (match && match.length >= 3) {
				var dateString = "Week" + match[1] + " " + match[2][0].toUpperCase() + match[2].substring(1);  // "Week1 Tue"
				if (DATES[dateString]) {
					var date = DATES[dateString];
					var year = date.getYear();
					if (year < 1000) {
						// non-IE<=8 returns year since 1900; IE<=8 returns actual year
						year += 1900;
					}
					var newDateString = "delayed_" +
							MONTHS_OF_YEAR[date.getMonth()].substring(0, 3) +
							"_" + date.getDate() +
							"_" + year;

					var newClassStr = classStr.replace(datePattern, newDateString);
					element.removeClassName(classStr);
					element.addClassName(newClassStr);
					return;
				}
			}
		}

		// fallback: stores show-date as inner HTML
		if (skipInnerHTML !== true) {
			var text = element.getAttribute("rel") || getTextContent(element);
			var datePattern = /week[ ]*(\d+)[ ]*(sun|mon|tue|wed|thu|fri|sat)/i;
			var match = text.match(datePattern);
			if (match && match.length >= 3) {
				// regex match returns array of numbered parts matching () groups
				// match = ["Week2 Fri", "2", "Fri"]
				var dateString = "Week" + match[1] + " " + match[2][0].toUpperCase() + match[2].substring(1);
				if (DATES[dateString]) {
					var date = DATES[dateString];
					var newDateString;
					if (element.hasClassName("insertdatelong")) {
						newDateString = DAYS_OF_WEEK[date.getDay()] + ", " + MONTHS_OF_YEAR[date.getMonth()] + " " + date.getDate();
					} else if (element.hasClassName("insertdateshort")) {
						newDateString = DAYS_OF_WEEK[date.getDay()].substring(0, 3) +
								" " + MONTHS_OF_YEAR[date.getMonth()].substring(0, 3) +
								" " + date.getDate();
					} else {
						var year = date.getYear();
						if (year < 1000) {
							// non-IE<=8 returns year since 1900; IE<=8 returns actual year
							year += 1900;
						}
						newDateString = DAYS_OF_WEEK[date.getDay()].substring(0, 3) +
								" " + MONTHS_OF_YEAR[date.getMonth()].substring(0, 3) +
								" " + date.getDate() +
								" " + year;
					}
					text = text.replace(datePattern, newDateString);
					setTextContent(element, text);
				}
			}
		}
	}

	// for debugging; shows all delayed elements on the page.
	function showDelayed() {
		$$(".delayed").each(function(element) {
			$(element).show();
			$(element).style.visibility = "";
		});
	}

	// Colors every other row of tables that have the class 'color_alternating_rows'
	// or the class 'zebrastripe'.
	function zebraStripes() {
		var rows = $$("table.color_alternating_rows tr");
		for (var i = 0; i < rows.length; i += 2) {
			// color every other row gray
			rows[i].addClassName("evenrow");
		}
		rows = $$("table.zebrastripe tr");
		for (var i = 0; i < rows.length; i += 2) {
			// color every other row gray
			rows[i].addClassName("evenrow");
		}
	}


	// This is "main", the main function of code that runs when the page loads.
	document.observe("dom:loaded", function() {
		// handle zebra-striping on HTML tables as indicated
		// zebraStripes();

		// make links with the class 'popup' show in a new window
		$$("a.popup").each(function(element) {
			element.target = "_blank";
		});

		// staff page: insert SL/TA info
		if (location.pathname.match(/\/staff/)) {
			insertStaffTableRows();
		}

		// place links around email addresses
		emailAddressLinks();

		// place links to maps to campus buildings
		buildingMapLinks();

		// make some links show a reminder about the course honor code
		honorCodeLinks();

		// auto-insert dates on "date" spans
		$$(".insertdate, .insertdateshort, .dateshort, .showdate, .hidedate").each(replaceRelativeDate);
		insertDates();

		// show any "delayed" links such as labs or sections to pop up on certain dates
		processDelayedContent();

		// put up a warning if this is not the current quarter's web site
		checkPageOutOfDate();

		if (location.href.match(/lectures.[s]?html/)) {
			plusMinusLinks();   // set +/- icons to expand/collapse various groups of files
			highlightCurrentDate();

			if ($("maximizelink")) {
				$("maximizelink").href = "#";
				$("maximizelink").observe("click", function() {
					this.siblings().each(Element.toggle);
					return false;
				});
			}
		}
	});
})();


// UTILITY FUNCTIONS

/** Deletes whitespace from the front of the given string. */
String.prototype.ltrim = function() {
	for (var k = 0; k < this.length && this.charAt(k) <= " "; k++) {
	}
	return this.substring(k, this.length);
};

/** Inserts spaces at the front of the given string until it reaches the given length,
 *  then returns the padded string.
 */
String.prototype.padL = function(length, nbsp) {
	var str = this;
	while (str.length < length) {
		str = (nbsp ? "&nbsp;" : " ") + str;
	}
	return str;
};

/** Inserts spaces at the front of the given string until it reaches the given length,
 *  then returns the padded string.
 */
String.prototype.padR = function(length, nbsp) {
	var str = this;
	while (this.length < length) {
		str = str + (nbsp ? "&nbsp;" : " ");
	}
	return str;
};

/** Deletes whitespace from the end of the given string. */
String.prototype.rtrim = function() {
	for ( var j = this.length - 1; j >= 0 && this.charAt(j) <= " "; j--) {
	}
	return this.substring(0, j + 1);
};

/** Converts escape sequences into visible characters. */
String.prototype.toPrintableChar = function() {
	if (this == "\n") {
		return "\\n";
	} else if (this == "\r") {
		return "\\r";
	} else if (this == "\t") {
		return "\\t";
	} else if (this == " " && navigator.userAgent.match(/Internet Explorer/)) {
		return "&nbsp;";   // IE sux
	} else {
		return this;
	}
};

/** Deletes whitespace from the front and end of the given string. */
String.prototype.trim = function() {
	return this.ltrim().rtrim();
};

/**
* code stolen from
* http://github.com/kangax/protolicious/blob/5b56fdafcd7d7662c9d648534225039b2e78e371/event.simulate.js
*
* Event.simulate(@element, eventName[, options]) -> Element
*
* - @element: element to fire event on
* - eventName: name of event to fire (only MouseEvents and HTMLEvents interfaces are supported)
* - options: optional object to fine-tune event properties - pointerX, pointerY, ctrlKey, etc.
*
* $('foo').simulate('click'); // => fires "click" event on an element with id=foo
*
**/

(function(){
    var eventMatchers = {
        'HTMLEvents': /^(?:load|unload|abort|error|select|change|submit|reset|focus|blur|resize|scroll)$/,
        'MouseEvents': /^(?:click|mouse(?:down|up|over|move|out))$/
    }
    var defaultOptions = {
        pointerX: 0,
        pointerY: 0,
        button: 0,
        ctrlKey: false,
        altKey: false,
        shiftKey: false,
        metaKey: false,
        bubbles: true,
        cancelable: true
    }

    Event.simulate = function(element, eventName) {
        var options = Object.extend(defaultOptions, arguments[2] || { });
        var oEvent, eventType = null;

        element = $(element);

        for (var name in eventMatchers) {
            if (eventMatchers[name].test(eventName)) {
                eventType = name;
                break;
            }
        }

        if (!eventType) {
            throw new SyntaxError('Only HTMLEvents and MouseEvents interfaces are supported');
        }

        if (document.createEvent) {
            oEvent = document.createEvent(eventType);
            if (eventType == 'HTMLEvents') {
                oEvent.initEvent(eventName, options.bubbles, options.cancelable);
            }
            else {
                oEvent.initMouseEvent(eventName, options.bubbles, options.cancelable, document.defaultView,
                    options.button, options.pointerX, options.pointerY, options.pointerX, options.pointerY,
                    options.ctrlKey, options.altKey, options.shiftKey, options.metaKey, options.button, element);
            }
            element.dispatchEvent(oEvent);
        }
        else {
            options.clientX = options.pointerX;
            options.clientY = options.pointerY;
            oEvent = Object.extend(document.createEventObject(), options);
            element.fireEvent('on' + eventName, oEvent);
        }
        return element;
    };

    Element.disableLink = function(element) {
        if (!element || element.disabled) {
            return element;
        }

        element.disabled = true;
        //element.oldBG = element.getStyle("background-color");
        element.oldFG = "black";
        if (typeof(element.getStyle) !== "undefined") {
            // BUGFIX: element foreground colors were not being restored after disable/re-enable
            element.oldFG = element.getStyle("color");
        }
        //element.style.backgroundColor = "#ccc";
        element.style.color = "#888";

        // if element is a link (a), must turn off its href for now
        if (element.tagName && element.tagName.toLowerCase() == "a") {
            // element.oldHref = element.href;
            // element.href = "";
        }

        return element;
    };

    Element.enableLink = function(element) {
        if (!element || !element.disabled) {
            return element;
        }

        element.disabled = false;
        //element.style.backgroundColor = element.oldBG;
        element.style.color = element.oldFG;

        // if element is a link (a), must turn back on its href
        if (element.tagName && element.tagName.toLowerCase() == "a") {
            // element.href = element.oldHref;
            // element.oldHref = undefined;
        }

        return element;
    };

    Element.linkIsEnabled = function(element) {
        if (!element) {
            return element;
        }
        return !element.disabled;
    };

    Element.tooltip = function(element, tooltipText, autoHide, timeout) {
        if (!tooltipText) {
            if (!element.title) {
                return element;
            }
            tooltipText = "&nbsp;&nbsp;" + element.title + "&nbsp;&nbsp;";
        }

        var tooltip = document.createElement("span");
        tooltip.update(tooltipText);
        tooltip.className = "tooltip";

        var offset = element.cumulativeOffset();
        // tooltip.style.position = "fixed";
        tooltip.style.left = offset.left + 2 + "px";
        tooltip.style.top = offset.top + element.getHeight() + 2 + "px";

        // changed tooltip positioning to be closer to the original element
        // (old code failed when element was wrapped inside a position:abs/rel element)
        // tooltip.style.top = "20px";

        // element.parentNode.insertBefore(tooltip, element.nextSibling === undefined ? element : element.nextSibling);
        document.body.appendChild(tooltip);

        var hideTooltip = function() {
            new Effect.BlindLeft(tooltip);
        };

        // Why would anybody ever not auto-hide?
        if (true || autoHide) {
            if (!timeout) {
                timeout = 5000;
            }
            setTimeout(hideTooltip, timeout);
        }

        var highlightCount = 3;
        var highlightAgainObj = {
        	afterFinish: function() {
        		tooltip.highlight();
        	}
        };
        tooltip.highlight(highlightAgainObj);


        return tooltip;
    };

    Element.addMethods({
        simulate: Event.simulate,
        disableLink: Element.disableLink,
        enableLink: Element.enableLink,
        linkIsEnabled: Element.linkIsEnabled,
        tooltip: Element.tooltip
    });
})();
