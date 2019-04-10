local requestIDKey = KEYS[1]
local lockCountKey = KEYS[2]

local currentRequestID = ARGV[1]
local expireTimeTTL = ARGV[2]

local lockSet = redis.call('setnx',requestIDKey,currentRequestID)

if lockSet == 1
then
	redis.call('expire',requestIDKey,expireTimeTTL)
	redis.call('set',lockCountKey,1)
	redis.call('expire',lockCountKey,expireTimeTTL)
	return 1
else
	local oldRequestID = redis.call('get',requestIDKey)
	if currentRequestID == oldRequestID
	then
		redis.call('incr',lockCountKey)
		redis.call('expire',requestIDKey,expireTimeTTL)
		redis.call('expire',lockCountKey,expireTimeTTL)
		return 1
	else return 0
	end
end
