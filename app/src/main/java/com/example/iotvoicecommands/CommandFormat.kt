package com.example.iotvoicecommands

import com.fasterxml.jackson.annotation.JsonProperty

data class CommandFormat(@JsonProperty("command") var command : String)
