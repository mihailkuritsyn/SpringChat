$(document).ready(function () {

    function ChatViewModel() {

        var that = this;

        that.userName = ko.observable('');
        that.userColor = ko.observable('');
        that.chatContent = ko.observable('');
        that.chatMembers = ko.observable('');
        that.message = ko.observable('');
        that.messageIndex = ko.observable(0);
        that.activePollingXhr = ko.observable(null);

        var keepPolling = false;

        that.joinChat = function () {
            if (that.userName().trim() != '') {
                keepPolling = true;
                addMemmber();
                pollForMessages();
            }
        }

        function addMemmber() {
            var form = $("#postMessageForm");
            $.ajax({
                url: form.attr("action") + "/addMember", type: "POST",
                data: 'login=<font color=\"' + that.userColor() + '\">' + that.userName().trim() + '</font><br>',
                error: function (xhr) {
                    console.error("Error adding new member to chat. status=" + xhr.status + ", statusText=" + xhr.statusText);
                }
            });
        }

        function getMembers(form) {
            $.ajax({
                url: form.attr("action") + "/getMembers", type: "GET", cache: false,
                success: function (members) {
                    that.chatMembers('');
                    for (var i = 0; i < members.length; i++) {
                        that.chatMembers(that.chatMembers() + members[i]);
                    }
                },
                error: function (xhr) {
                    if (xhr.statusText != "abort" && xhr.status != 503) {
                        resetUI();
                        console.error("Unable to retrieve chat members. Chat ended.");
                    }
                },
            })
        }

        function pollForMessages() {
            if (!keepPolling) {
                return;
            }
            var form = $("#joinChatForm");
            that.activePollingXhr(
                $.ajax({
                    url: form.attr("action"), type: "GET", data: form.serialize(), cache: false,
                    success: function (messages) {
                        for (var i = 0; i < messages.length; i++) {
                            that.chatContent(that.chatContent() + messages[i] + "\n");
                            that.messageIndex(that.messageIndex() + 1);
                        }
                    },
                    error: function (xhr) {
                        if (xhr.statusText != "abort" && xhr.status != 503) {
                            resetUI();
                            console.error("Unable to retrieve chat messages. Chat ended.");
                        }
                    },
                    complete: pollForMessages
                })
            );

            getMembers(form);


            $('#message').focus();
        }

        that.postMessage = function () {
            if (that.message().trim() != '') {
                var form = $("#postMessageForm");
                $.ajax({
                    url: form.attr("action"), type: "POST",
                    data: createMessage(),
                    error: function (xhr) {
                        console.error("Error posting chat message: status=" + xhr.status + ", statusText=" + xhr.statusText);
                    }
                });
                that.message('');
            }
        }

        that.leaveChat = function () {
            that.activePollingXhr(null);
            resetUI();
            this.userName('');
        }

        function resetUI() {
            keepPolling = false;
            that.activePollingXhr(null);
            that.message('');
            that.userColor('');
            that.messageIndex(0);
            that.chatContent('');
            that.chatMembers('');
        }

        function createMessage() {
            var date = new Date();
            return 'message=<font color=\"' + that.userColor() + '\">' +
                date.getHours() + ':' + date.getHours() + ' ' + that.userName() + ': ' +
                $('#postMessageForm input[name=message]').val() + '</font><br>';
        }

    }

    //Activate knockout.js
    ko.applyBindings(new ChatViewModel());

});


