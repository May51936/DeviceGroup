(function(){
	var logo = document.getElementById("logo");
    var in_data = document.getElementById("in-data");
    var out_data = document.getElementById("out-data");
    var current_data = document.getElementById("current-data");
	var time_since = document.getElementById("time-since");
	const month = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

	function setTime() {
		var now = new Date();
		var year = now.getFullYear();
		var month_name = month[now.getMonth()];
		var date = now.getDate();
		var hour = now.getHours();
		hour = ("0" + hour).slice(-2);
		var minute = now.getMinutes();
		minute = ("0" + minute).slice(-2);
		time_since.innerHTML = "Since " + hour + ":" + minute + " on " + month_name + " " + date + ", " + year;
	}

    function updateData(){
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState==4 && xmlhttp.status==200)
        	{

        		in_data.innerHTML = JSON.parse(xmlhttp.responseText).in;
        		out_data.innerHTML = JSON.parse(xmlhttp.responseText).out;
        		current_data.innerHTML = JSON.parse(xmlhttp.responseText).total;
        	}
        };
        xmlhttp.open("GET", "/getPeopleData", true);
        xmlhttp.send();
    }

	function resetData() {
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState==4 && xmlhttp.status==200)
        	{
                alert(xmlhttp.responseText);
        	}
        };
        xmlhttp.open("GET", "/resetPeopleData", true);
        xmlhttp.send();
		setTime();
	}

    setTime();
    setInterval(updateData, 1000);
	logo.onclick = function() {resetData()};
})();