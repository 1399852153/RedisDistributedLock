local requestIDKey = KEYS[1]
local lockCountKey = KEYS[2]

local currentRequestID = ARGV[1]

if redis.call('get', KEYS[1]) == ARGV[1] 
then
	local currentCount = redis.call('decr',lockCountKey)
	if currentCount == 0
	then redis.call('del',requestIDKey)
	return 1
else 
	return 0 end