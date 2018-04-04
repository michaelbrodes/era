'use strict';
+function() {
    /**
     * Called when a a.rbtn is clicked, it performs an ajax call to the /api/
     * @param e
     */
    function deleteAssignment(e) {
        var targetSiblings = e.target.parentElement.children;
        // get the input element that is a sibling to the rbtn
        var uuid = targetSiblings[targetSiblings.length - 1].value;

        var restCall = new XMLHttpRequest();
        restCall.open("DELETE", "/api/assignment/" + uuid);
        restCall.send(null);
        restCall.onreadystatechange = function () {
            if(restCall.readyState === XMLHttpRequest.DONE && restCall.status === 204) {
                // success! remove the tr housing this rbtn
                var parentRow = e.target.parentNode.parentNode;
                var parentTable = parentRow.parentNode;
                parentTable.removeChild(parentRow);
            } else if (restCall.readyState === XMLHttpRequest.DONE && restCall.status !== 200) {
                alert("Couldn't delete assignment in the database. HTTP status " + restCall.status + ". See server log for details.");
            }
        };
    }

    var deleteButtons = document.getElementsByClassName("rbtn");
    for (var i = 0; i < deleteButtons.length; i++) {
        // noinspection JSUnresolvedFunction
        deleteButtons[i].addEventListener("click", deleteAssignment);
    }
}();