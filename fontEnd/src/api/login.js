/**
 * Created by qi on 2017/9/3.
 */
import fetch from '@/utils/fetch'

export function login (username, password) {
  const data = {
    username: username,
    password: password
  }
  return fetch({
    url: '/auth',
    method: 'post',
    data
  })
}

export function validateLoginUsername (loginUsername) {
  return fetch({
    url: '/validateLoginUsername/' + loginUsername,
    method: 'get'
  })
}

export function validateMailbox (mailbox) {
  return fetch({
    url: '/validateMailbox',
    method: 'post',
    data: {
      mailbox: mailbox
    }
  })
}

export function send (mailbox) {
  return fetch({
    url: '/send',
    method: 'post',
    data: {
      mailbox: mailbox
    }
  })
}

export function reset (mailbox, password, verificationCode) {
  return fetch({
    url: '/reset',
    method: 'post',
    data: {
      mailbox: mailbox,
      password: password,
      verificationCode:verificationCode
    }
  })
}

export function register (loginUsername, username, mailbox, password) {
  return fetch({
    url: '/register',
    method: 'post',
    data: {
      loginUsername: loginUsername,
      username: username,
      mailbox: mailbox,
      password: password
    }
  })
}

export function getUserOwnInfo () {
  return fetch({
    url: '/userOwnInfo',
    method: 'get'
  })
}
